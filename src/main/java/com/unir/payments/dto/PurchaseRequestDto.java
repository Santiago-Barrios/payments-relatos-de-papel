package com.unir.payments.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

public record PurchaseRequestDto(
        @NotEmpty(message = "La compra debe incluir al menos un libro")
        @Valid
        List<PurchaseItemRequestDto> items
) {}
