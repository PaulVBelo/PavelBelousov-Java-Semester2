package com.example.demo3.rating.records;

public record BookRatingRequestDTO(long bookId,
                                   String title,
                                   String authorFirstName,
                                   String authorLastName) {
}
