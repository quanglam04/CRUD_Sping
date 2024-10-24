package com.example.bestStore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.bestStore.model.Product;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Long> {
    List<Product> findAll();

    Product save(Product product);

    Optional findById(Long id);

}
