package com.example.marketplace.dto;

import com.example.marketplace.entity.Order;
import com.example.marketplace.entity.OrderItem;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderResponse {
    private Long id;
    private Long userId;
    private String status;
    private LocalDateTime orderDate;
    private List<OrderItemDto> items;

    public OrderResponse(Order order, List<OrderItem> items) {
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.status = order.getStatus();
        this.orderDate = order.getOrderDate();
        this.items = items.stream()
            .map(OrderItemDto::new)
            .collect(Collectors.toList());
    }

    @Data
    public static class OrderItemDto {
        private Long productId;
        private String productName;
        private int quantity;
        private double price;

        public OrderItemDto(OrderItem item) {
            this.productId = item.getProduct().getId();
            this.productName = item.getProduct().getName();
            this.quantity = item.getQuantity();
            this.price = item.getPrice().doubleValue();
        }
    }
}