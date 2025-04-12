package com.example.marketplace.controller;

import com.example.marketplace.entity.Product;
import com.example.marketplace.entity.Review;
import com.example.marketplace.entity.User;
import com.example.marketplace.exception.ResourceNotFoundException;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.ReviewRepository;
import com.example.marketplace.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewController(ReviewRepository reviewRepository,
                            ProductRepository productRepository,
                            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Создать отзыв
    @PostMapping
    public ResponseEntity<Review> createReview(@RequestParam Long userId,
                                               @RequestParam Long productId,
                                               @RequestParam int rating,
                                               @RequestParam(required = false) String comment) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setRating(rating);
        review.setComment(comment != null ? comment : "");

        Review savedReview = reviewRepository.save(review);

        recalcProductRating(product);

        return ResponseEntity.status(201).body(savedReview);
    }

    // Получить отзывы по конкретному товару
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviewsByProduct(@PathVariable Long productId,
                                                            @RequestParam(required = false) String sortBy) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        List<Review> reviews;
        if ("rating".equalsIgnoreCase(sortBy)) {
            reviews = reviewRepository.findByProductOrderByRatingDesc(product);
        } else {
            reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);
        }

        return ResponseEntity.ok(reviews);
    }

    // Вспомогательный метод для пересчёта среднего рейтинга
    private void recalcProductRating(Product product) {
        List<Review> allReviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);
        if (allReviews.isEmpty()) {
            product.setAverageRating(BigDecimal.ZERO);
        } else {
            double avg = allReviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            BigDecimal average = BigDecimal.valueOf(avg).setScale(2, BigDecimal.ROUND_HALF_UP);
            product.setAverageRating(average);
        }
        productRepository.save(product);
    }
}
