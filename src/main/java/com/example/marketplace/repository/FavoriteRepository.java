package com.example.marketplace.repository;

import com.example.marketplace.entity.Favorite;
import com.example.marketplace.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // Избранное для конкретного пользователя
    List<Favorite> findByUser(User user);

    // Проверка, есть ли уже запись (userId + productId) в избранном
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
