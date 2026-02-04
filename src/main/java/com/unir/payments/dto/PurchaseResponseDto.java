package com.unir.payments.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.unir.payments.domain.Purchase;

public record PurchaseResponseDto(
        Long id,
        LocalDateTime purchaseDate,
        List<PurchaseItemResponseDto> items
) {
    public static PurchaseResponseDto from(Purchase purchase) {
        List<PurchaseItemResponseDto> items = purchase.getItems().stream()
                .map(item -> new PurchaseItemResponseDto(item.getBookId(), item.getQuantity()))
                .toList();
        return new PurchaseResponseDto(
                purchase.getId(),
                purchase.getPurchaseDate(),
                items
        );
    }
}
