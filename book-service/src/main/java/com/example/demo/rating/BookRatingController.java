package com.example.demo.rating;

import com.example.demo.exceptions.ApiError;
import com.example.demo.models.authors.Author;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.rating.records.BookRatingRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController("BookRatingController")
@RequestMapping("api")
public class BookRatingController {
  private final BookRepository bookRepository;
  private final BookRatingProducer producer;

  @Autowired
  public BookRatingController(BookRepository bookRepository,
                              BookRatingProducer producer) {
    this.bookRepository = bookRepository;
    this.producer = producer;
  }

  @GetMapping("/books/{id}/rating")
  @Transactional
  public void requestBookRating(@PathVariable("id") Long id) throws JsonProcessingException {
    Book book = bookRepository.findById(id).orElseThrow();
    Author author = book.getAuthor();
    producer.requestBookRating(new BookRatingRequestDTO(
        book.getId(),
        book.getTitle(),
        author.getFirstName(),
        author.getLastName()
        )
    );
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.NOT_FOUND);
  }
}
