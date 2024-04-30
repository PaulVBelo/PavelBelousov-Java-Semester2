package com.example.demo.controller;

import com.example.demo.exceptions.ApiError;
import com.example.demo.models.authors.AuthorRepository;
import com.example.demo.models.authors.records.AuthorOnlyDTO;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.tags.Tag;
import com.example.demo.models.tags.TagRepository;
import com.example.demo.models.books.records.BookWithAuthorDTO;
import com.example.demo.models.tags.records.TagResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@RestController("tagController")
@RequestMapping("/api/tags")
public class TagController {
  private final TagRepository tagRepository;

  @Autowired
  public TagController(TagRepository tagRepository) {
    this.tagRepository = tagRepository;
  }

  @Transactional
  @PostMapping
  public TagResponseDTO createTag(@RequestBody @Valid Tag tag) {
    Tag savedTag =  tagRepository.save(tag);
    return new TagResponseDTO(savedTag.getId(), savedTag.getName(), new ArrayList<>());
  }

  @Transactional
  @GetMapping("/{id}")
  public TagResponseDTO getTag(@PathVariable("id") Long id) {
    //Get в задании не было, я его сделал для себя.
    Tag tag = tagRepository.findById(id).orElseThrow();
    Set<Book> books = tag.getBooks();
    return new TagResponseDTO(tag.getId(), tag.getName(),
        books.stream().map(b -> new BookWithAuthorDTO(
            b.getId(),
            b.getTitle(),
            new AuthorOnlyDTO(
                b.getAuthor().getId(),
                b.getAuthor().getFirstName(),
                b.getAuthor().getLastName()
            )
        )).collect(Collectors.toList()));
  }

  @Transactional
  @PutMapping("/{id}")
  public void updateTag(@PathVariable("id") Long id,
                           @RequestBody @Valid Tag toUpdate) {
    Tag tag = tagRepository.findById(id).orElseThrow();
    tag.setName(toUpdate.getName());
    tagRepository.save(tag);
  }

  @Transactional
  @DeleteMapping("/{id}")
  public void deleteTag(@PathVariable("id") Long id) {
    tagRepository.deleteById(id);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> sqlExceptionHandler(SQLException e) {
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
}
