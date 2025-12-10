package com.pos_billing.pos_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponse {

    private Long orderId;

    private String customerName;
    private String customerPhone;
    private String customerAddress;

    private Double totalAmount;
    private String paymentMethod;
    private LocalDateTime createdAt;

    private List<OrderItemResponse> items;
}
