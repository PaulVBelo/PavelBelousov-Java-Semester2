package com.example.demo.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest(
    classes = {OutboxScheduler.class},
    properties = {"purchase.topic-to-send-message=test-topic"}
)
@Import({KafkaAutoConfiguration.class, OutboxSchedulerTest.ObjectMapperTestConfig.class})
@Testcontainers
class OutboxSchedulerTest {
  @TestConfiguration
  static class ObjectMapperTestConfig {
    @Bean
    public ObjectMapper objectMapper() {
      return new ObjectMapper();
    }
  }

  @Container
  @ServiceConnection
  public static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

  @Autowired
  ObjectMapper objectMapper;

  @MockBean
  OutboxRepository repository;

  @Autowired
  @InjectMocks
  OutboxScheduler scheduler;

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws Exception {
    Mockito.when(repository.findAll()).thenReturn(Arrays.asList(new OutboxRecord(
        objectMapper.writeValueAsString(new PurchaseResponse(1l, 2l, true))
    )));

    assertDoesNotThrow(() -> scheduler.processOutbox());

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          try {
            PurchaseResponse message = objectMapper.readValue(record.value(), PurchaseResponse.class);
            assertEquals(1l, message.id());
            assertEquals(2l, message.bookId());
            assertTrue(message.success());
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
  }
}