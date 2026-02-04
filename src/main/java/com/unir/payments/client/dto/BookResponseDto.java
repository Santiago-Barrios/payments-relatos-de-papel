package com.unir.payments.client.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private LocalDate publicationDate;
    private String category;
    private Long isbn;
    private Integer valoration;
    private Boolean isVisible;
    private Integer currentStock;
}
