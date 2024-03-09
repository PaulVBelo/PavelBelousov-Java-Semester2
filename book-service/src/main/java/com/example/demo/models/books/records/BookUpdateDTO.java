package com.example.demo.models.books.records;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookUpdateDTO(@NotNull String title,
                            @NotNull String author,
                            @NotNull List<String> tags) {
}
