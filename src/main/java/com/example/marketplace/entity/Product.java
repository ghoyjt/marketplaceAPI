package com.example.marketplace.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
  
    @Column(nullable = false)
    private String name;
  
    @Column(columnDefinition = "TEXT")
    private String description;
  
    @Column(nullable = false)
    private BigDecimal price;
  
    private String category;

    @Column(name = "average_rating", precision = 3, scale = 2)
    private BigDecimal averageRating;

    // ===== Геттеры и сеттеры =====

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }
    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }
}
