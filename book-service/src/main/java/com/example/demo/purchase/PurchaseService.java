package com.example.demo.purchase;

import com.example.demo.models.authors.Author;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.purchase.outbox.OutboxRecord;
import com.example.demo.purchase.outbox.OutboxRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class PurchaseService {
  private final OutboxRepository outboxRepository;
  private final BookRepository bookRepository;
  private final ObjectMapper objectMapper;

  @Autowired
  public PurchaseService(OutboxRepository outboxRepository, BookRepository bookRepository, ObjectMapper objectMapper) {
    this.outboxRepository = outboxRepository;
    this.bookRepository = bookRepository;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public void prepareBookPurchaseRequest(Long bookId) {
    Book book = bookRepository.findById(bookId).orElseThrow();
    Author author = book.getAuthor();
    book.buyBook();
    bookRepository.save(book);
    try {
      BookToPurchase request = new BookToPurchase(ThreadLocalRandom.current().nextLong(1, Long.MAX_VALUE),
          bookId, book.getTitle(), author.getFirstName(), author.getLastName());
      outboxRepository.save(new OutboxRecord(objectMapper.writeValueAsString(request)));
    } catch (JsonProcessingException e) {
      throw new PurchaseBookException("Error while reaching outbox", e);
    }
  }
}
