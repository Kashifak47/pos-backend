package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.dto.PaymentVerifyRequest;
import com.pos_billing.pos_backend.service.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final RazorpayService razorpayService;

    // ⭐ 1️⃣ Frontend → create Razorpay order
   @PostMapping("/create")
public ResponseEntity<?> createPaymentOrder(@RequestBody Map<String, Object> body) {

    if (!body.containsKey("amount")) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Amount missing"));
    }

    Object amountObj = body.get("amount");

    if (!(amountObj instanceof Number)) {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Amount must be a number"));
    }

    int amount = ((Number) amountObj).intValue();

    return ResponseEntity.ok(razorpayService.createRazorpayOrder(amount));
}


    // ⭐ 2️⃣ Frontend → verify + create order + reduce stock
    @PostMapping("/verify-and-create")
    public ResponseEntity<?> verifyAndCreate(
            @RequestBody PaymentVerifyRequest request,
            Authentication auth) {
        return ResponseEntity.ok(razorpayService.verifyAndCreateOrder(request, auth));
    }
}
