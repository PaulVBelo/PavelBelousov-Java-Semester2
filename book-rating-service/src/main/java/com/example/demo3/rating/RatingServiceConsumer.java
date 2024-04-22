package com.example.demo3.rating;

import com.example.demo3.rating.records.BookRatingRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class RatingServiceConsumer {

  private final ObjectMapper objectMapper;
  private final MessageProcessor processor;

  private static final Logger LOGGER = LoggerFactory.getLogger(RatingServiceConsumer.class);

  @Autowired
  public RatingServiceConsumer(ObjectMapper objectMapper, MessageProcessor processor) {
    this.objectMapper = objectMapper;
    this.processor = processor;
  }

  @KafkaListener(topics = {"${topic-to-consume-message}"})
  public void rateBook(String message, Acknowledgment acknowledgement) {
    try {
      BookRatingRequestDTO bookToRate = objectMapper.readValue(message, BookRatingRequestDTO.class);
      LOGGER.info("Retrieved message {}", message);
      processor.process(message);
      acknowledgement.acknowledge();
    } catch (JsonProcessingException e) {
      // Не должно произойти. В худшем случае просто не отвечаем.
    }
  }
}