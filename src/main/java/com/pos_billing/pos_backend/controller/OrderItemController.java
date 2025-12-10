package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.service.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
// @CrossOrigin("*")
public class OrderItemController {

    private final OrderItemService orderItemService;

    @PutMapping("/{itemId}/status")
    public ResponseEntity<?> update(
            @PathVariable Long itemId,
            @RequestParam String status) {
        try {
            return ResponseEntity.ok(
                    Map.of("message", orderItemService.updateItemStatus(itemId, status)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
