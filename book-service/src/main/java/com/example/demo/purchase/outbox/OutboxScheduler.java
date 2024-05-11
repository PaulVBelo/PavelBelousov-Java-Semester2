package com.example.demo.purchase.outbox;

import com.example.demo.exceptions.RateBookException;
import com.example.demo.purchase.PurchaseBookException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class OutboxScheduler {
  private final KafkaTemplate<String, String> kafkaTemplate;
  private final String topic;
  private final OutboxRepository outboxRepository;

  @Autowired
  public OutboxScheduler(KafkaTemplate<String, String> kafkaTemplate,
                         @Value("${purchase.topic-to-send-message}") String topic,
                         OutboxRepository outboxRepository) {
    this.kafkaTemplate = kafkaTemplate;
    this.topic = topic;
    this.outboxRepository = outboxRepository;
  }

  @Transactional
  @Scheduled(fixedDelay = 10000)
  public void processOutbox() {
    List<OutboxRecord> result = outboxRepository.findAll();
    for (OutboxRecord record: result) {
      CompletableFuture<SendResult<String, String>> sendResult = kafkaTemplate.send(topic, record.getData());
      try {
        sendResult.get(2, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Unexpected thread interruption", e);
      } catch (ExecutionException e) {
        throw new PurchaseBookException("Couldn't send message to Kafka", e);
      } catch (TimeoutException e) {
        throw new PurchaseBookException("Couldn't send message to Kafka due to timeout", e);
      }
    }
    outboxRepository.deleteAll(result);
  }
}
