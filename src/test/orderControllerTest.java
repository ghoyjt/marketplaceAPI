package com.example.marketplace;

import com.example.marketplace.dto.CreateOrderRequest;
import com.example.marketplace.entity.Product;
import com.example.marketplace.entity.User;
import com.example.marketplace.repository.OrderRepository;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    private final ObjectMapper mapper = new ObjectMapper();
    private User user;
    private Product product;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        user = new User();
        user.setName("John Doe");
        user.setEmail("john@example.com");
        userRepository.save(user);

        product = new Product();
        product.setName("Sample Product");
        product.setDescription("A sample product for testing");
        product.setPrice(BigDecimal.TEN);
        product.setCategory("Test");
        productRepository.save(product);
    }

    @Test
    public void testCreateAndGetOrder() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setUserId(user.getId());
        CreateOrderRequest.OrderItemRequest itemRequest = new CreateOrderRequest.OrderItemRequest();
        itemRequest.setProductId(product.getId());
        itemRequest.setQuantity(2);
        request.setItems(Collections.singletonList(itemRequest));

        mvc.perform(post("/api/v1/orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.status").value("NEW"));

        mvc.perform(get("/api/v1/orders/user/" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].userId").value(user.getId()));
    }
}