package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.model.Product;
import com.pos_billing.pos_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/customer/products")
@RequiredArgsConstructor
// @CrossOrigin("*")
public class CustomerProductController {

    private final ProductService productService;

    // LIST PRODUCTS OR FILTER BY CATEGORY
    // @GetMapping
    // public ResponseEntity<List<Product>> getProducts(
    // @RequestParam(required = false) String category
    // ) {
    // if (category != null && !category.equalsIgnoreCase("All")) {
    // return ResponseEntity.ok(productService.getByCategory(category));
    // }
    // return ResponseEntity.ok(productService.getAll());
    // }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        return ResponseEntity.ok(productService.getFilteredProducts(page, size, category, search, sort));
    }

    // CUSTOMER CATEGORY LIST
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        Set<String> categories = new HashSet<>();

        for (Product p : productService.getAll()) {
            if (p.getCategory() != null) {
                categories.add(p.getCategory());
            }
        }

        List<String> sorted = new ArrayList<>(categories);
        Collections.sort(sorted);
        sorted.add(0, "All");

        return ResponseEntity.ok(sorted);
    }
}
