package com.example.demo.controller;

import com.example.demo.models.authors.Author;
import com.example.demo.models.authors.AuthorRepository;
import com.example.demo.exceptions.ApiError;
import com.example.demo.models.authors.records.AuthorResponseDTO;
import com.example.demo.models.books.records.BookWithTagsDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController("authorController")
@RequestMapping("/api/authors")
public class AuthorController {
  private final AuthorRepository authorRepository;

  @Autowired
  public AuthorController(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  @Transactional
  @PostMapping
  public AuthorResponseDTO createAuthor(@RequestBody @Valid Author author) {
    Author savedAuthor = authorRepository.save(author);
    return new AuthorResponseDTO(
        savedAuthor.getId(),
        savedAuthor.getFirstName(),
        savedAuthor.getLastName(),
        new ArrayList<>());
  }

  @Transactional
  @GetMapping("/{id}")
  public AuthorResponseDTO getAuthor(@PathVariable("id") Long id) {
    //Get в задании не было, я его сделал для себя.
    Author author = authorRepository.findById(id).orElseThrow();
    author.getBooks().iterator();
    return new AuthorResponseDTO(
        author.getId(),
        author.getFirstName(),
        author.getLastName(),
        author.getBooks().stream()
            .map(book -> new BookWithTagsDTO(
                book.getId(),
                book.getTitle(),
                book.getTags().stream()
                    .map(tag -> tag.getName())
                    .collect(Collectors.toList())
            ))
            .collect(Collectors.toList()));
  }

  @Transactional
  @PutMapping("/{id}")
  public void updateAuthor(@PathVariable("id") Long id,
                           @RequestBody @Valid Author toUpdate) {
    Author author = authorRepository.findById(id).orElseThrow();
    author.setFirstName(toUpdate.getFirstName());
    author.setLastName(toUpdate.getLastName());
    authorRepository.save(author);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public void deleteAuthor(@PathVariable("id") Long id) {
    authorRepository.deleteById(id);
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
