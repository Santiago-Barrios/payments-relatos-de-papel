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
    private Double price;
    private String imageUrl;
    private String description;
    private String editorial;
    private String language;
    private Integer pages;
    private Integer edition;
    private String biography;
    private String authorPhotoUrl;
}
