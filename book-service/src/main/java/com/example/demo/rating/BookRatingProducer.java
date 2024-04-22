package com.example.demo.rating;

import com.example.demo.exceptions.RateBookException;
import com.example.demo.rating.records.BookRatingRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class BookRatingProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final String topic;

  @Autowired
  public BookRatingProducer(KafkaTemplate<String, String> kafkaTemplate,
                            ObjectMapper objectMapper,
                            @Value("${topic-to-send-message}") String topic) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.topic = topic;
  }

  public void requestBookRating(BookRatingRequestDTO bookToRate) throws JsonProcessingException {
    String message = objectMapper.writeValueAsString(bookToRate);
    CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, message);
    try {
      sendResult.get(2, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Unexpected thread interruption", e);
    } catch (ExecutionException e) {
      throw new RateBookException("Couldn't send message to Kafka", e);
    } catch (TimeoutException e) {
      throw new RateBookException("Couldn't send message to Kafka due to timeout", e);
    }
  }
}
