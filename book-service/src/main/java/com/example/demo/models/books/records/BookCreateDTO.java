package com.example.demo.models.books.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookCreateDTO(@NotBlank(message = "Book title has to be filled") String title,
                            @NotNull(message = "Book's authorId has to be filled") Long authorId,
                            @NotNull(message = "At least one tag has to be filled") List<String> tags) {
}
