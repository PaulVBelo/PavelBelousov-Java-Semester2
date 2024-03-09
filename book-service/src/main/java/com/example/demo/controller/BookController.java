package com.example.demo.controller;

import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.books.records.BookCreateDTO;
import com.example.demo.models.books.records.BookDTO;
import com.example.demo.models.books.records.BookUpdateDTO;
import com.example.demo.models.exceptions.ApiError;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
public class BookController {

  private final BookRepository bookRepository;

  @Autowired
  public BookController(BookRepository bookRepository) {
    this.bookRepository = bookRepository;
  }

  @GetMapping("/books/{id}")
  public Book getBook(@PathVariable("id") Long id) {
    return bookRepository.getBook(id).orElseThrow();
  }

  @PostMapping("/books")
  public BookDTO createBook(@RequestBody @Valid BookCreateDTO request) {
    return bookRepository.addBook(request);
  }

  @PutMapping("/books/{id}")
  public void updateBook(@PathVariable("id") Long id, @RequestBody @Valid BookUpdateDTO request) {
    bookRepository.updateBook(id, request);
  }

  @DeleteMapping("/books/{id}")
  public void deleteBook(@PathVariable("id") Long id) {
    bookRepository.deleteBook(id);
  }

  @GetMapping("/books/filter")
  public List<Book> getBooksByTag(@RequestParam(name = "tag",required = false) String tagReq) {
    return bookRepository.getBooksByTag(tagReq);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> methodArgumentNotValidResponse(MethodArgumentNotValidException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.BAD_REQUEST);
  }
}