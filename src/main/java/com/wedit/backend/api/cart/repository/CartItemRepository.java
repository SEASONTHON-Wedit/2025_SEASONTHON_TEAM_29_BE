package com.wedit.backend.api.cart.repository;

import com.wedit.backend.api.cart.entity.Cart;
import com.wedit.backend.api.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 Cart에 속한 모든 아이템을 연관 엔티티와 함께 조회
    @Query("SELECT ci FROM CartItem ci " +
            "JOIN FETCH ci.product p " +
            "JOIN FETCH p.vendor v " +
            "JOIN FETCH v.region r " +
            "LEFT JOIN FETCH v.logoMedia " +
            "WHERE ci.cart = :cart")
    List<CartItem> findAllWithDetailsByCart(@Param("cart") Cart cart);
}
