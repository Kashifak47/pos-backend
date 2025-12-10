package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.service.OrderService;
import com.pos_billing.pos_backend.service.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
// @CrossOrigin("*")
public class AdminDashboardController {

    private final OrderService orderService;
    private final CustomerService customerService;

    // DASHBOARD STATS: totalSales, totalOrders, totalCustomers
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {

        long totalOrders = orderService.countOrders();
        double totalSales = orderService.totalSalesAmount();
        long totalCustomers = customerService.countCustomers();

        Map<String, Object> response = new HashMap<>();
        response.put("totalSales", totalSales == 0 ? 0 : totalSales);
        response.put("totalOrders", totalOrders);
        response.put("totalCustomers", totalCustomers);

        return ResponseEntity.ok(response);
    }

    // WEEKLY SALES CHART DATA
    @GetMapping("/weekly-sales")
    public ResponseEntity<?> getWeeklySales() {

        List<Map<String, Object>> data = orderService.getWeeklySales();

        return ResponseEntity.ok(data);
    }
}
