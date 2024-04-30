package com.example.demo.rating;

import com.example.demo.rating.records.BookRatingResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookRatingConsumer {
  private final MessageProcessor messageProcessor;
  private final ObjectMapper objectMapper;

  @Autowired
  public BookRatingConsumer(MessageProcessor messageProcessor, ObjectMapper objectMapper) {
    this.messageProcessor = messageProcessor;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  @Transactional
  public void rateBook(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    var result = objectMapper.readValue(message, BookRatingResponseDTO.class);
    messageProcessor.process(message);
    acknowledgment.acknowledge();
  }
}
