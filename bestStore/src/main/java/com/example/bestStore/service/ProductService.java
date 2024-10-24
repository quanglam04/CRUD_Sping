package com.example.bestStore.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.bestStore.model.Product;
import com.example.bestStore.repository.ProductsRepository;

@Service
public class ProductService {
    private final ProductsRepository productsRepository;

    public ProductService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public List<Product> fetchAllProduct() {
        return this.productsRepository.findAll();
    }

    public void saveProduct(Product prd) {
        this.productsRepository.save(prd);
    }

    public Optional findById(long id) {
        return this.productsRepository.findById(id);
    }

    public void deleteProduct(Product product) {
        this.productsRepository.delete(product);
    }
}
