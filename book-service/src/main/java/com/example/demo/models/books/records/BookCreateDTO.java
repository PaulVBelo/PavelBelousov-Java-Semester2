package com.example.demo.models.books.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookCreateDTO(@NotBlank(message = "Book title has to be filled") String title,
                            @NotBlank(message = "Book author has to be filled") String author,
                            @NotNull(message = "Book must have at least 1 tag") List<String> tags) {
}
