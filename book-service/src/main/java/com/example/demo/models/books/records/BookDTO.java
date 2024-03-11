package com.example.demo.models.books.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BookDTO(@NotNull Long id,
                      @NotBlank String title,
                      @NotBlank String author,
                      @NotNull List<String> tags) {
  //Вспомогательный класс для CreateBook (Без него только этот метод работал неправильно)
}
