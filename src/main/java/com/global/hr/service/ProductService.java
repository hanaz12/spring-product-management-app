package com.global.hr.service;

import com.global.hr.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAllProducts();

    void deleteProduct(int id);

    void saveProduct(Product product);

    Product getProduct(int id);
}
