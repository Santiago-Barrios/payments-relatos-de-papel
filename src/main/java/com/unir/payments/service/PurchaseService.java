package com.unir.payments.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import com.unir.payments.client.BookCatalogClient;
import com.unir.payments.client.dto.BookRequestDto;
import com.unir.payments.client.dto.BookResponseDto;
import com.unir.payments.domain.Purchase;
import com.unir.payments.exception.PurchaseValidationException;
import com.unir.payments.repository.PurchaseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final BookCatalogClient bookCatalogClient;
    private final PurchaseRepository purchaseRepository;

    /**
     * Registra una compra de uno o varios libros.
     * Valida que todos los libros existan, tengan stock suficiente y estén visibles.
     * Si algún libro no cumple, la compra no se completa.
     * Tras validar, persiste la compra y actualiza el stock en el microservicio buscador.
     */
    @Transactional
    public Purchase registerPurchase(List<PurchaseItemRequest> items) {
        if (items == null || items.isEmpty()) {
            throw new PurchaseValidationException("La compra debe incluir al menos un libro");
        }

        List<BookResponseDto> validatedBooks = validateAllBooks(items);

        Purchase purchase = new Purchase();
        purchase.setPurchaseDate(LocalDateTime.now());

        for (int i = 0; i < items.size(); i++) {
            PurchaseItemRequest item = items.get(i);
            purchase.addItem(item.bookId(), item.quantity());
        }

        Purchase savedPurchase = purchaseRepository.save(purchase);

        updateStockInCatalog(items, validatedBooks);

        return savedPurchase;
    }

    private List<BookResponseDto> validateAllBooks(List<PurchaseItemRequest> items) {
        List<BookResponseDto> books = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (PurchaseItemRequest item : items) {
            try {
                BookResponseDto book = bookCatalogClient.getBookById(item.bookId());

                if (Boolean.FALSE.equals(book.getIsVisible())) {
                    errors.add("El libro con id %d no está disponible (oculto)".formatted(item.bookId()));
                } else if (book.getCurrentStock() == null || book.getCurrentStock() < item.quantity()) {
                    errors.add("Stock insuficiente para el libro id %d (disponible: %d, solicitado: %d)"
                            .formatted(item.bookId(),
                                    book.getCurrentStock() != null ? book.getCurrentStock() : 0,
                                    item.quantity()));
                } else {
                    books.add(book);
                }
            } catch (HttpClientErrorException.NotFound e) {
                errors.add("El libro con id %d no existe en el catálogo".formatted(item.bookId()));
            }
        }

        if (!errors.isEmpty()) {
            throw new PurchaseValidationException(String.join("; ", errors));
        }

        return books;
    }

    private void updateStockInCatalog(List<PurchaseItemRequest> items, List<BookResponseDto> books) {
        for (int i = 0; i < items.size(); i++) {
            PurchaseItemRequest item = items.get(i);
            BookResponseDto book = books.get(i);

            int newStock = book.getCurrentStock() - item.quantity();

            BookRequestDto updateRequest = new BookRequestDto();
            updateRequest.setTitle(book.getTitle());
            updateRequest.setAuthor(book.getAuthor());
            updateRequest.setPublicationDate(book.getPublicationDate());
            updateRequest.setCategory(book.getCategory());
            updateRequest.setIsbn(book.getIsbn());
            updateRequest.setValoration(book.getValoration());
            updateRequest.setIsVisible(book.getIsVisible());
            updateRequest.setCurrentStock(newStock);

            bookCatalogClient.updateBook(book.getId(), updateRequest);
        }
    }

    public record PurchaseItemRequest(Long bookId, Integer quantity) {}
}
