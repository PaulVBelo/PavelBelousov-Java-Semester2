package com.example.demo.models.books.records;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookUpdateDTO(String title,
                            Long authorId,
                            List<String> tags) {
}
