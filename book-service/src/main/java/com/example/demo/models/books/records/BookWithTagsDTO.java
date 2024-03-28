package com.example.demo.models.books.records;

import java.util.List;

public record BookWithTagsDTO(
  Long bookId,
  String title,
  List<String> tags
){
  // Переноска данных книг по автору
}
