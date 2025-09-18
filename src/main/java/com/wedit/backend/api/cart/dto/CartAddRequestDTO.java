package com.wedit.backend.api.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CartAddRequestDTO {

    @NotNull
    private Long productId;

    @NotNull
    private LocalDateTime executionDateTime;
}
