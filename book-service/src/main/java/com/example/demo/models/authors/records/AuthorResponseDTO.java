package com.example.demo.models.authors.records;

import com.example.demo.models.books.records.BookWithTagsDTO;

import java.util.List;

public record AuthorResponseDTO(
    Long id,
    String firstName,
    String lastName,
    List<BookWithTagsDTO> books
) {
}
