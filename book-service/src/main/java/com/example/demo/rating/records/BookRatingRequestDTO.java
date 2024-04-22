package com.example.demo.rating.records;

public record BookRatingRequestDTO(long bookId,
                                   String title,
                                   String authorFirstName,
                                   String authorLastName) {
}
