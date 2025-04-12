package com.example.marketplace;

import com.example.marketplace.entity.Product;
import com.example.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mvc;
  
    @Autowired
    private ProductRepository productRepository;
  
    private final ObjectMapper mapper = new ObjectMapper();
  
    @BeforeEach
    public void setup() {
        productRepository.deleteAll();
    }
  
    @Test
    public void testCreateAndGetProduct() throws Exception {
        Product product = new Product();
        product.setName("Тестовый товар");
        product.setDescription("Просто тест");
        product.setPrice(BigDecimal.valueOf(15.50));
        product.setCategory("Тест");
      
        mvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(product)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists());
      
        mvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Тестовый товар"));
    }
}