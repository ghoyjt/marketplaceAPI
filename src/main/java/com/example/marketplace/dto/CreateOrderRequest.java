package com.example.marketplace.dto;

import java.util.List;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private Long userId;
    private List<OrderItemRequest> items;

    @Data
    public static class OrderItemRequest {
        private Long productId;
        private int quantity;
    }
}