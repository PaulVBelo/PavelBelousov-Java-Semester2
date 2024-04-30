package com.example.demo.service.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Book(
    @NotBlank String title,
    @NotNull Author author
) {
}
