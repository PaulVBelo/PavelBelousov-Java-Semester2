package com.example.demo3.rating;

import com.example.demo3.rating.records.BookRatingResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = {RatingServiceProducer.class},
    properties = {"topic-to-send-message=test-topic"}
)
@Import({KafkaAutoConfiguration.class, RatingServiceProducerTest.ObjectMapperTestConfig.class})
@Testcontainers
class RatingServiceProducerTest {

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
  private RatingServiceProducer producer;

  @Autowired
  private ObjectMapper objectMapper;


  @Test
  void shouldSendMessageToKafkaSuccessfully() throws Exception {
    assertDoesNotThrow(() -> producer.sendRatingResponse(
        new BookRatingResponseDTO(1l, 10)
        )
    );

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          try {
            BookRatingResponseDTO message = objectMapper.readValue(record.value(), BookRatingResponseDTO.class);
            assertEquals(1l, message.bookId());
            assertEquals(10, message.bookRating());
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
  }
}