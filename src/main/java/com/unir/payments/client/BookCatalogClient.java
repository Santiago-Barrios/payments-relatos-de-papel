package com.unir.payments.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.unir.payments.client.dto.BookRequestDto;
import com.unir.payments.client.dto.BookResponseDto;

/**
 * Cliente para el microservicio buscador (books-ms) usando el nombre registrado en Eureka.
 * La URL usa el service-id "search" - nunca IP ni puerto directo.
 */
@Component
public class BookCatalogClient {

    private static final String BOOKS_SERVICE_ID = "search";
    private static final String BASE_URL = "http://" + BOOKS_SERVICE_ID + "/api/v1/books";

    private final RestClient restClient;

    public BookCatalogClient(@Qualifier("loadBalancedRestClientBuilder") RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    public BookResponseDto getBookById(Long id) {
        return restClient.get()
                .uri(BASE_URL + "/{id}", id)
                .retrieve()
                .body(BookResponseDto.class);
    }

    public BookResponseDto updateBook(Long id, BookRequestDto request) {
        return restClient.put()
                .uri(BASE_URL + "/{id}", id)
                .body(request)
                .retrieve()
                .body(BookResponseDto.class);
    }
}
