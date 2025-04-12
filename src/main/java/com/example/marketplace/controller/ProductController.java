package com.example.marketplace.controller;

import com.example.marketplace.entity.Product;
import com.example.marketplace.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {
  
    private final ProductRepository productRepository;
  
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
  
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(@RequestParam(required = false) String category) {
        List<Product> products = (category != null && !category.isEmpty()) 
                ? productRepository.findByCategory(category)
                : productRepository.findAll();
        return ResponseEntity.ok(products);
    }
  
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        if (product.getName() == null || product.getPrice() == null) {
            return ResponseEntity.badRequest().build();
        }
        Product saved = productRepository.save(product);
        return ResponseEntity.status(201).body(saved);
    }
  
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id).map(existing -> {
            existing.setName(product.getName());
            existing.setDescription(product.getDescription());
            existing.setPrice(product.getPrice());
            existing.setCategory(product.getCategory());
            productRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
  
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id).map(product -> {
            productRepository.delete(product);
            return ResponseEntity.ok().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}