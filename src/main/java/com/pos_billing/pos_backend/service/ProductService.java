package com.pos_billing.pos_backend.service;

import com.pos_billing.pos_backend.model.Product;
import com.pos_billing.pos_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    // Get by category
    public List<Product> getByCategory(String category) {
        if (category.equalsIgnoreCase("All")) {
            return productRepository.findAll();
        }
        return productRepository.findByCategory(category);
    }

    public Product addProduct(Product p) {
        return productRepository.save(p);
    }

    public Product updateProduct(Long id, Product updated) {
        return productRepository.findById(id).map(product -> {
            product.setName(updated.getName());
            product.setPrice(updated.getPrice());
            product.setStock(updated.getStock());
            product.setCategory(updated.getCategory());
            product.setImageUrl(updated.getImageUrl());
            return productRepository.save(product);
        }).orElse(null);
    }

    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id))
            return false;
        productRepository.deleteById(id);
        return true;
    }

    public Map<String, Object> getFilteredProducts(int page, int size, String category, String search, String sort) {

        Pageable pageable = PageRequest.of(page, size);

        List<Product> all = productRepository.findAll();

        // Filter by category
        if (category != null && !category.equalsIgnoreCase("All")) {
            all = all.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        // Search by name
        if (search != null && !search.isEmpty()) {
            all = all.stream()
                    .filter(p -> p.getName().toLowerCase().contains(search.toLowerCase()))
                    .toList();
        }

        // Sort
        if (sort != null) {
            switch (sort) {
                case "price_asc" -> all = all.stream()
                        .sorted(Comparator.comparing(Product::getPrice))
                        .toList();

                case "price_desc" -> all = all.stream()
                        .sorted(Comparator.comparing(Product::getPrice).reversed())
                        .toList();

                case "category_asc" -> all = all.stream()
                        .sorted(Comparator.comparing(Product::getCategory))
                        .toList();

                case "category_desc" -> all = all.stream()
                        .sorted(Comparator.comparing(Product::getCategory).reversed())
                        .toList();
            }
        }

        // Pagination manually
        int start = page * size;
        int end = Math.min(start + size, all.size());
        List<Product> paginated = start > all.size() ? List.of() : all.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("products", paginated);
        response.put("total", all.size());

        return response;
    }
}
