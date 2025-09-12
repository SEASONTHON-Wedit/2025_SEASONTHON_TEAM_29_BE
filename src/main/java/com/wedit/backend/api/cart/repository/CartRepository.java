package com.wedit.backend.api.cart.repository;

import com.wedit.backend.api.cart.entity.Cart;
import com.wedit.backend.api.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByMemberId(Long memberId);

    Optional<Cart> findByMember(Member member);
}
