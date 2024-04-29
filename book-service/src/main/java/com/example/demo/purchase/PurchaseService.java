package com.example.demo.purchase;

import com.example.demo.models.books.BookRepository;
import com.example.demo.outbox.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
