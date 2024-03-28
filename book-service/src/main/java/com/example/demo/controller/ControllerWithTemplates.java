package com.example.demo.controller;

import com.example.demo.models.authors.AuthorRepository;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.books.records.TemplatableBookDTO;
import com.example.demo.models.tags.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api")
public class ControllerWithTemplates {
  private final BookRepository bookRepository;

  @Autowired
  public ControllerWithTemplates(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @Transactional
  @GetMapping("/books")
  public String getBooksAll(Model model) {
    List<Book> bookList = bookRepository.findAll();
    model.addAttribute("books", bookList.stream()
        .map(b -> new TemplatableBookDTO(
            b.getId(),
            b.getTitle(),
            b.getAuthor().getFirstName() + " " + b.getAuthor().getLastName(),
            b.getTags().stream().map(t -> t.getName()).collect(Collectors.toList())
            )
        ).collect(Collectors.toList())
    );
    return "books";
  }
}