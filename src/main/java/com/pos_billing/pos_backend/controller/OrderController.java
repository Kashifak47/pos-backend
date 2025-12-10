package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.dto.CreateOrderRequest;
import com.pos_billing.pos_backend.dto.OrderResponse;
import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.model.User;
import com.pos_billing.pos_backend.service.OrderService;
import com.pos_billing.pos_backend.repository.CustomerRepository;
import com.pos_billing.pos_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
// @CrossOrigin("*")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;


    // ⭐ ADMIN → GET ALL ORDERS (with customerName, address, items)
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }


    // ⭐ CUSTOMER → CREATE ORDER (auto customer detection)
    @PostMapping("/create")
    public ResponseEntity<?> create(
            @RequestBody CreateOrderRequest req,
            Authentication auth) {

        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(orderService.createOrder(req, auth));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }


    // ⭐ CUSTOMER → MY ORDERS
    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders(Authentication auth) {

        try {
            String email = auth.getName();
            User user = userRepository.findByEmail(email).orElseThrow();

            Customer customer = customerRepository.findByUserId(user.getId());
            if (customer == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Customer profile not found"));
            }

            List<OrderResponse> orders = orderService.getOrdersByCustomer(customer.getId());
            return ResponseEntity.ok(orders);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
