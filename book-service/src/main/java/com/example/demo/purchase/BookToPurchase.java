package com.example.demo.purchase;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Да, это очередной DTO с однотипным содержанием.
public record BookToPurchase(
    @NotNull Long requestId,
    @NotNull Long bookId,
    @NotBlank String title,
    @NotBlank String authorFirstName,
    @NotBlank String authorLastName) {
}
