package com.pos_billing.pos_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentVerifyRequest {

    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private double totalAmount;
    private String method; // "UPI" or "Card"

    private List<PaymentItemDto> items;
}
