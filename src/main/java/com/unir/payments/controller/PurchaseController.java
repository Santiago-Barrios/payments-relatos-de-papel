package com.unir.payments.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unir.payments.domain.Purchase;
import com.unir.payments.dto.PurchaseRequestDto;
import com.unir.payments.dto.PurchaseResponseDto;
import com.unir.payments.service.PurchaseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping
    public ResponseEntity<PurchaseResponseDto> registerPurchase(@Valid @RequestBody PurchaseRequestDto request) {
        List<PurchaseService.PurchaseItemRequest> items = request.items().stream()
                .map(dto -> new PurchaseService.PurchaseItemRequest(dto.bookId(), dto.quantity()))
                .toList();

        Purchase purchase = purchaseService.registerPurchase(items);
        return ResponseEntity.status(HttpStatus.CREATED).body(PurchaseResponseDto.from(purchase));
    }
}
