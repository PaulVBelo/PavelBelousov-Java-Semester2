package com.example.demo.models.tags.records;

import com.example.demo.models.books.records.BookWithAuthorDTO;

import java.util.List;

public record TagResponseDTO(Long id,
                             String name,
                             List<BookWithAuthorDTO> books) {
}
