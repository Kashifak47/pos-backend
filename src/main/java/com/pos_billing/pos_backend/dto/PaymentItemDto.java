package com.pos_billing.pos_backend.dto;

import lombok.Data;

@Data
public class PaymentItemDto {
    private Long productId;
    private int qty;
    private double price;
}
