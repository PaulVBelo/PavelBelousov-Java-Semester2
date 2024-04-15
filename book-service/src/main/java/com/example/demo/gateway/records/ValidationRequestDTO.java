package com.example.demo.gateway.records;

import jakarta.validation.constraints.NotBlank;

public record ValidationRequestDTO(
    @NotBlank(message = "Bad request: firstName is Blank") String firstName,
    @NotBlank(message = "Bad request: firstName is Blank") String lastName,
    @NotBlank(message = "Bad request: firstName is Blank") String title
) {
}
