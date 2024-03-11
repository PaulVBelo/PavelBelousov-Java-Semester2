package com.example.demo.controller;

import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/books")
public class BookControllerWithTemplates {
  private final BookRepository bookRepository;

  @Autowired
  public BookControllerWithTemplates(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping("")
  private String getBooksAll(Model model) {
    List<Book> bookList = this.bookRepository.getBooksAll();
    model.addAttribute("books", bookList);
    return "books";
  }
}