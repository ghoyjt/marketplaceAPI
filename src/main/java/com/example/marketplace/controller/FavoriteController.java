package com.example.marketplace.controller;

import com.example.marketplace.entity.Favorite;
import com.example.marketplace.entity.Product;
import com.example.marketplace.entity.User;
import com.example.marketplace.exception.ResourceNotFoundException;
import com.example.marketplace.repository.FavoriteRepository;
import com.example.marketplace.repository.ProductRepository;
import com.example.marketplace.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public FavoriteController(FavoriteRepository favoriteRepository,
                              UserRepository userRepository,
                              ProductRepository productRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    // Добавить товар в избранное
    @PostMapping
    public ResponseEntity<Favorite> addToFavorites(@RequestParam Long userId,
                                                   @RequestParam Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        boolean exists = favoriteRepository.existsByUserIdAndProductId(userId, productId);
        if (exists) {
            return ResponseEntity.badRequest().build();
        }

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setProduct(product);
        Favorite savedFavorite = favoriteRepository.save(favorite);

        return ResponseEntity.status(201).body(savedFavorite);
    }

    // Получить все избранные товары конкретного пользователя
    @GetMapping("/{userId}")
    public ResponseEntity<List<Favorite>> getFavorites(@PathVariable Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return ResponseEntity.ok(favorites);
    }

    // Удалить товар из избранного
    @DeleteMapping
    public ResponseEntity<Void> removeFromFavorites(@RequestParam Long userId,
                                                    @RequestParam Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<Favorite> userFavorites = favoriteRepository.findByUser(user);
        Favorite toDelete = userFavorites.stream()
                .filter(f -> f.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Favorite not found for user " + userId + " and product " + productId
                ));

        favoriteRepository.delete(toDelete);
        return ResponseEntity.ok().build();
    }
}
