package com.example.demo.service.records;

import jakarta.validation.constraints.NotBlank;

public record ValidationRequestDTO(
    @NotBlank(message = "Bad request: firstName is Blank") String firstName,
    @NotBlank(message = "Bad request: lastName is Blank") String lastName,
    @NotBlank(message = "Bad request: title is Blank") String title
) {
}
