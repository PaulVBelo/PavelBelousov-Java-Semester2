package com.example.demo.purchase;

import com.example.demo.models.books.InboxRecord;
import com.example.demo.models.books.InboxRepository;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.books.Status;
import com.example.demo.purchase.outbox.PurchaseResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PurchaseListener {
  // О НЕТ!!! Мне опять нужна дедупликация!!!! НЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕТ!!!!!!!!!!
  private final InboxRepository inboxRepository;
  private final ObjectMapper objectMapper;
  private final BookRepository bookRepository;

  @Autowired
  public PurchaseListener(InboxRepository inboxRepository, ObjectMapper objectMapper, BookRepository bookRepository) {
    this.inboxRepository = inboxRepository;
    this.objectMapper = objectMapper;
    this.bookRepository = bookRepository;
  }

  @KafkaListener(topics = {"${purchase.topic-to-consume-message}"})
  @Transactional
  public void confirmPurchase(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
     PurchaseResponseDTO result = objectMapper.readValue(message, PurchaseResponseDTO.class);
     acknowledgment.acknowledge();

    Optional<InboxRecord> checkUnique = inboxRepository.findById(result.id());
    Book bookPurchased = bookRepository.findById(result.bookId()).orElseThrow();
    if (checkUnique.isEmpty()) {
      if (result.success()) {
        bookPurchased.setStatus(Status.BOUGHT);
      } else {
        bookPurchased.setStatus(Status.PAYMENT_CANCELLED);
      }
      bookRepository.save(bookPurchased);
    }
  }
}
