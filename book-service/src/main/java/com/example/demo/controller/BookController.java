package com.example.demo.controller;

import com.example.demo.exceptions.ApiError;
import com.example.demo.models.authors.Author;
import com.example.demo.models.authors.AuthorRepository;
import com.example.demo.models.authors.records.AuthorOnlyDTO;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.books.records.BookCreateDTO;
import com.example.demo.models.books.records.BookResponseDTO;
import com.example.demo.models.books.records.BookUpdateDTO;
import com.example.demo.models.tags.Tag;
import com.example.demo.models.tags.TagRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController("bookController")
@RequestMapping("/api/books")
public class BookController {
  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;
  private final TagRepository tagRepository;

  @Autowired
  public BookController(BookRepository bookRepository, AuthorRepository authorRepository, TagRepository tagRepository) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.tagRepository = tagRepository;
  }

  // Добавление реализовано сразу с тегами, так как мне это показалось логичным.
  // В бд ссылка на автора помечена как NOT NULL (да и удалять её - нарушение одной из нормальных форм), поэтому смена автора реализована напрямую в PUT

  @Transactional
  @PostMapping
  public BookResponseDTO createBook(@RequestBody @Valid BookCreateDTO toCreate) {
    Author author = authorRepository.findById(toCreate.authorId()).orElseThrow();
    author.getBooks().iterator();
    List<Tag> tagList = new ArrayList<>();
    for (String s: toCreate.tags()) {
      Tag tag;
      Optional<Tag> optTag = tagRepository.findByName(s);
      if (optTag.isPresent()) {
        tagList.add(optTag.get());
      } else {
        tagList.add(tagRepository.save(new Tag(s)));
      }
    }
    Book book = bookRepository.save(new Book(toCreate.title(), author));
    for (Tag tag: tagList) {
      book.getTags().add(tag);
    }
    return new BookResponseDTO(book.getId(), book.getTitle(),
        new AuthorOnlyDTO(author.getId(), author.getFirstName(), author.getLastName()),
        book.getTags().stream().map(t -> t.getName()).collect(Collectors.toList()));
  }

  @Transactional
  @GetMapping("/{id}")
  public BookResponseDTO getBook(@PathVariable("id") Long id) {
    //Get в задании не было, я его сделал для себя.
    Book book = bookRepository.findById(id).orElseThrow();
    return new BookResponseDTO(book.getId(), book.getTitle(),
        new AuthorOnlyDTO(
            book.getAuthor().getId(),
            book.getAuthor().getFirstName(),
            book.getAuthor().getLastName()),
        book.getTags().stream().map(t -> t.getName()).collect(Collectors.toList()));
  }

  @Transactional
  @PutMapping("/{id}")
  public void updateBook(@PathVariable("id") Long id,
                         @RequestBody BookUpdateDTO toUpdate) {
    Book book = bookRepository.findById(id).orElseThrow();
    if (toUpdate.authorId()!=null) {
      // Я не сделал удаление связки.
      Author authorToSet = authorRepository.findById(toUpdate.authorId()).orElseThrow();
      book.setAuthor(authorToSet);
      bookRepository.save(book);
    }
    if (toUpdate.title()!=null && toUpdate.title().length()>0) {book.setTitle(toUpdate.title());}
    if (toUpdate.tags()!=null) {
      book.getTags().clear();
      for (String s: toUpdate.tags()) {
        if (s.length()>0) {
          Optional<Tag> optTag = tagRepository.findByName(s);
          Tag tag = optTag.isPresent() ? optTag.get() : tagRepository.save(new Tag(s));
          book.getTags().add(tag);
        }
      }
    }
    bookRepository.save(book);
  }


  @Transactional
  @DeleteMapping("/{id}")
  public void deleteBook(@PathVariable("id") Long id) {
    bookRepository.deleteById(id);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> constraintViolationExceptionResponse(ConstraintViolationException e) {
    return new ResponseEntity(new ApiError(e.getConstraintViolations()
        .stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(", "))),
        HttpStatus.BAD_REQUEST);
  }
}
