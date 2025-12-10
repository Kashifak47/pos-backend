package com.pos_billing.pos_backend.dto;

import lombok.Data;

@Data
public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private String status;
}
