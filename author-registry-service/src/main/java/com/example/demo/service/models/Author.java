package com.example.demo.service.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Author(
    @NotBlank String firstName,
    @NotBlank String lastName
) {
}
