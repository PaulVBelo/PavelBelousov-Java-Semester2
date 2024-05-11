package com.example.demo.outbox;

public record PurchaseResponse(Long id,
                               Long bookId,
                               Boolean success) {
}
