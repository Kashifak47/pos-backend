package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/customers")
// @CrossOrigin("*")
public class AdminCustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);

        if (customer == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(customer);
    }

    @PostMapping
    public ResponseEntity<?> addCustomer(@RequestBody Map<String, Object> body) {

        Customer customer = new Customer();
        customer.setName((String) body.get("name"));
        customer.setPhone((String) body.get("phone"));
        customer.setEmail((String) body.get("email"));
        customer.setAddress((String) body.get("address"));

        String password = (String) body.get("password");

        Customer saved = customerService.addCustomer(customer, password);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @RequestBody Customer updatedCustomer) {

        Customer updated = customerService.updateCustomer(id, updatedCustomer);

        if (updated == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable Long id) {

        Customer existing = customerService.getById(id);

        if (existing == null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Customer not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        customerService.deleteCustomer(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Customer deleted successfully");

        return ResponseEntity.ok(response);
    }
}
