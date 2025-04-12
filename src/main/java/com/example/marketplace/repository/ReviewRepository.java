package com.example.marketplace.repository;

import com.example.marketplace.entity.Product;
import com.example.marketplace.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // Все отзывы по продукту, отсортированные по дате создания убыванием
    List<Review> findByProductOrderByCreatedAtDesc(Product product);

    // Все отзывы по продукту, отсортированные по рейтингу убыванием
    List<Review> findByProductOrderByRatingDesc(Product product);
}
