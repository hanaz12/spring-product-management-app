package com.global.hr.controller;

import com.global.hr.model.Product;
import com.global.hr.model.ProductDto;
import com.global.hr.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping({"", "/"})
    public String showProductsList(Model model) {
        List<Product> products = productService.findAllProducts();
        model.addAttribute("products", products);
        return "products/index";
    }

    @GetMapping("/delete")
    public String deleteProduct(@RequestParam("productId") int productId) {
        productService.deleteProduct(productId);
        return "redirect:/products";
    }

    @GetMapping("/create")
    public String createProduct(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/CreateProduct";

    }

    @PostMapping("/create")
    public String createProduct(@Valid @ModelAttribute("productDto") ProductDto productDto, BindingResult bindingResult) {
        if (productDto.getImageFile().isEmpty()) {
            bindingResult.addError(new FieldError("productDto", "imageFile", "Image file is empty"));
        }
        if (bindingResult.hasErrors()) {
            return "products/CreateProduct";
        }
        // save image on server
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFileName = createdAt.getTime() + "_" + image.getOriginalFilename();
        try {
            String uploadDir = "public/images";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectory(uploadPath);
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, Paths.get(uploadDir, storageFileName),
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("Exception" + e.getMessage());
        }
        Product product = new Product();
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());
        product.setCreatedAt(createdAt);
        product.setImageFileName(storageFileName);
        productService.saveProduct(product);

        return "redirect:/products";
    }


@GetMapping("/edit")
public String editProduct(@RequestParam("productId") int id, Model model) {
    Product product = productService.getProduct(id);
    model.addAttribute("product", product);

    ProductDto productDto = new ProductDto();
    productDto.setName(product.getName());
    productDto.setDescription(product.getDescription());
    productDto.setBrand(product.getBrand());
    productDto.setPrice(product.getPrice());
    productDto.setCategory(product.getCategory());
    model.addAttribute("productDto", productDto);

    return "products/EditProduct";
}

    @PostMapping("/edit")
    public String updateProduct(@Valid @ModelAttribute("productDto") ProductDto productDto,
                                BindingResult bindingResult, @RequestParam("id") int id, Model model) {
        Product product = productService.getProduct(id);
        model.addAttribute("product", product);

        if (bindingResult.hasErrors()) {
            return "products/EditProduct";
        }

        // تحديث الحقول
        product.setName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setBrand(productDto.getBrand());
        product.setPrice(productDto.getPrice());
        product.setCategory(productDto.getCategory());

        // تحديث الصورة إذا تم رفع صورة جديدة
        MultipartFile imageFile = productDto.getImageFile();
        if (!imageFile.isEmpty()) {
            try {
                String uploadDir = "public/images";
                String newFileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir, newFileName), StandardCopyOption.REPLACE_EXISTING);
                }
                product.setImageFileName(newFileName);
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }

        productService.saveProduct(product);
        return "redirect:/products";
    }
}




