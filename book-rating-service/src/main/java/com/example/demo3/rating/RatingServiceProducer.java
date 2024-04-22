package com.example.demo3.rating;

import com.example.demo3.rating.records.BookRatingResponseDTO;
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
public class RatingServiceProducer {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final ObjectMapper objectMapper;

  @Autowired
  public RatingServiceProducer(KafkaTemplate<String, String> kafkaTemplate,
                               @Value("${topic-to-send-message}") String topic,
                               ObjectMapper objectMapper) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.objectMapper = objectMapper;
  }

  public void sendRatingResponse(BookRatingResponseDTO responseDTO) {
    try {
      String messageResponse = objectMapper.writeValueAsString(responseDTO);
      CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, messageResponse);
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
    } catch (JsonProcessingException e) {
    }
  }
}
