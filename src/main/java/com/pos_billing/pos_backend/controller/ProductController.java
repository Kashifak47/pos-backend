// package com.pos_billing.pos_backend.controller;

// import com.pos_billing.pos_backend.model.Product;
// import com.pos_billing.pos_backend.service.ProductService;
// import lombok.RequiredArgsConstructor;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;

// @RestController
// @RequestMapping("/api/products")
// @RequiredArgsConstructor
// @CrossOrigin(origins = "*") // allow React frontend
// public class ProductController {

//     private final ProductService productService;

//      @GetMapping("/all")
//     public List<Product> getAll() {
//         return productService.getAll();
//     }

//     @GetMapping("/{id}")
//     public Product getById(@PathVariable Long id) {
//         return productService.getById(id);
//     }

//      @GetMapping("/category/{category}")
//     public List<Product> getByCategory(@PathVariable String category) {
//         return productService.getByCategory(category);
//     }

//     @PostMapping("/add")
//     public Product addProduct(@RequestBody Product p) {
//         return productService.addProduct(p);
//     }

//      @PutMapping("/update/{id}")
//     public Product update(@PathVariable Long id, @RequestBody Product p) {
//         return productService.updateProduct(id, p);
//     }

//     @DeleteMapping("/delete/{id}")
//     public String delete(@PathVariable Long id) {
//         boolean deleted = productService.deleteProduct(id);
//         return deleted ? "Product deleted successfully" : "Product not found";
//     }
// }
