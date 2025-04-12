package com.example.marketplace.controller;

import com.example.marketplace.dto.CreateOrderRequest;
import com.example.marketplace.dto.OrderResponse;
import com.example.marketplace.entity.Order;
import com.example.marketplace.entity.OrderItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.entity.User;
import com.example.marketplace.exception.ResourceNotFoundException;
import com.example.marketplace.repository.OrderItemRepository;
import com.example.marketplace.repository.OrderRepository;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderController(OrderRepository orderRepository,
                         OrderItemRepository orderItemRepository,
                         UserRepository userRepository,
                         ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<OrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        // Validate user
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("NEW");
        orderRepository.save(order);

        // Process order items
        List<OrderItem> orderItems = new ArrayList<>();
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Product not found: " + itemRequest.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(Math.max(1, itemRequest.getQuantity()));
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);

        return ResponseEntity.ok(new OrderResponse(order, orderItems));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        
        return ResponseEntity.ok(new OrderResponse(order, items));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        List<OrderResponse> responses = new ArrayList<>();
        
        for (Order order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
            responses.add(new OrderResponse(order, items));
        }
        
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        
        order.setStatus(status.toUpperCase());
        orderRepository.save(order);
        
        List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
        return ResponseEntity.ok(new OrderResponse(order, items));
    }
}