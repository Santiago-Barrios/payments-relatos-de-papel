package com.unir.payments.dto;

public record PurchaseItemResponseDto(
        Long bookId,
        Integer quantity
) {}
