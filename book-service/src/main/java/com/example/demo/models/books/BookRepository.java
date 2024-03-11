package com.example.demo.models.books;

import com.example.demo.models.books.records.BookCreateDTO;
import com.example.demo.models.books.records.BookDTO;
import com.example.demo.models.books.records.BookUpdateDTO;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
  Optional<Book> getBook(Long id);

  BookDTO addBook(BookCreateDTO toCreate);

  void updateBook(Long id, BookUpdateDTO toUpdate);

  void deleteBook(Long id);

  List<Book> getBooksAll();

  List<Book> getBooksByTag(String tag);

  void deleteAll();
}
