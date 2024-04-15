package com.example.demo.controller;

import com.example.demo.exceptions.ApiError;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.gateway.AuthorRegistryGateway;
import com.example.demo.gateway.records.ValidationRequestDTO;
import com.example.demo.gateway.records.ValidationResponseDTO;
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
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
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
  private final AuthorRegistryGateway gateway;

  @Autowired
  public BookController(BookRepository bookRepository,
                        AuthorRepository authorRepository,
                        TagRepository tagRepository,
                        AuthorRegistryGateway gateway) {
    this.bookRepository = bookRepository;
    this.authorRepository = authorRepository;
    this.tagRepository = tagRepository;
    this.gateway = gateway;
  }

  @Transactional
  @PostMapping
  public BookResponseDTO createBook(@RequestBody @Valid BookCreateDTO toCreate) {
    Author author = authorRepository.findById(toCreate.authorId()).orElseThrow();
    author.getBooks().iterator();
    ValidationResponseDTO validationResponse =
        gateway.validateBook(new ValidationRequestDTO(
            author.getFirstName(),
            author.getLastName(),
            toCreate.title()),
            UUID.randomUUID().toString()
        );
    if (!validationResponse.validationResult()) {
      throw new ValidationException("Bad Request: The creation form is Illegal");
    }
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
  @DeleteMapping("/{id}")
  public void deleteBook(@PathVariable("id") Long id) {
    bookRepository.deleteById(id);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> validationExceptionHandler(ValidationException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.NOT_FOUND);
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

  @ExceptionHandler
  public ResponseEntity<ApiError> requestNotPermittedResponse(RequestNotPermitted e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.BAD_GATEWAY);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> callNotPermittedExceptionResponse(CallNotPermittedException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.BAD_GATEWAY);
  }
}
