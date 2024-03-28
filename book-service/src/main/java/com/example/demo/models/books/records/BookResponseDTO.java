package com.example.demo.models.books.records;

import com.example.demo.models.authors.records.AuthorOnlyDTO;

import java.util.List;

public record BookResponseDTO(Long id,
                              String title,
                              AuthorOnlyDTO author,
                              List<String> tags) {
  //Вспомогательный класс для CreateBook (Без него только этот метод работал неправильно)
}
