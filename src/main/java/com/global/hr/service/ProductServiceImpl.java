package com.global.hr.service;

import com.global.hr.model.Product;
import com.global.hr.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {


    private ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public void deleteProduct(int id) {
         productRepository.deleteById(id);
    }

    @Override
    public Product getProduct(int id) {
        Optional<Product> result = productRepository.findById(id);
        Product product = null;
        if (result.isPresent()) {
            product = result.get();
        }
        return product;
    }


}
