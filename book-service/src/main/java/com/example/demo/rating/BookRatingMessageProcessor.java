package com.example.demo.rating;


import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.rating.records.BookRatingResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Component
public class BookRatingMessageProcessor implements MessageProcessor {
  private final BookRepository bookRepository;
  private final ObjectMapper objectMapper;

  public BookRatingMessageProcessor(BookRepository bookRepository,
                                    ObjectMapper objectMapper) {
    this.bookRepository = bookRepository;
    this.objectMapper = objectMapper;
  }

  @Override
  public void process(String message) {
    try {
      BookRatingResponseDTO responseMessage = objectMapper.readValue(message, BookRatingResponseDTO.class);
      Book book = bookRepository.findById(responseMessage.bookId()).orElseThrow();
      book.setRating(responseMessage.bookRating());
      bookRepository.save(book);
    } catch (JsonProcessingException e) {
    } catch (NoSuchElementException e) {
    }
  }
}
