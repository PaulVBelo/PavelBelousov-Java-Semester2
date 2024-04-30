package com.example.demo.models.books.records;

import com.example.demo.models.authors.records.AuthorOnlyDTO;

import java.util.List;

public record TemplatableBookDTO(Long id,
                                 String title,
                                 String author,
                                 List<String> tags) {
}
