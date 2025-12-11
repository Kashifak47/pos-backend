package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.model.Product;
import com.pos_billing.pos_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
// @CrossOrigin("*")
public class AdminProductController {

    private final ProductService productService;

    // GET ALL
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAll());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
        }
        return ResponseEntity.ok(product);
    }

    // ADD PRODUCT
    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(@RequestBody Product p) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.addProduct(p));
    }

    // UPDATE PRODUCT
    @PutMapping("update/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product p) {
        Product updated = productService.updateProduct(id, p);

        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
        }

        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);

        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "Product not found"));
        }

        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }
}
