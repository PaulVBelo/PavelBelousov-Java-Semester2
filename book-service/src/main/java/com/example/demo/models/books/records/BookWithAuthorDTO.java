package com.example.demo.models.books.records;

import com.example.demo.models.authors.records.AuthorOnlyDTO;

public record BookWithAuthorDTO(
    Long id,
    String title,
    AuthorOnlyDTO author
) {
  //Переноска данных книг по тегу
}
