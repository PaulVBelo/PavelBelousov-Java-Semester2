package com.example.demo.purchase.outbox;

public record PurchaseResponseDTO(Long id,
                                  Long bookId,
                                  Boolean success) {
}
