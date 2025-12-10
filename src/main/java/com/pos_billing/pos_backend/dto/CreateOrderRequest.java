package com.pos_billing.pos_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {

    // private String customerName;
    // private String customerPhone;
    // private String address;

    private String paymentMethod;
    private Double totalAmount;

    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private String productName;
        private Double price;
        private Integer quantity;
    }
}
