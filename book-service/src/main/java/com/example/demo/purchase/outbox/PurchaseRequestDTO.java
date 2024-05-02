package com.example.demo.purchase.outbox;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PurchaseRequestDTO(
    @NotNull Long requestId,

    @NotNull Long bookId,

    @NotBlank String title,

    @NotBlank String authorFirstName,

    @NotBlank String authorLastName
                                 ) {
}
