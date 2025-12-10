package com.pos_billing.pos_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.pos_billing.pos_backend.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
        List<Product> findByCategory(String category);
}   
