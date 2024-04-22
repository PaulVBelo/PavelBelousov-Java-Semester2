package com.example.demo.rating;

import com.example.demo.rating.records.BookRatingRequestDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.protocol.types.Field;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.retrytopic.DestinationTopic;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
    classes = {BookRatingProducer.class},
    properties = {"topic-to-send-message=test-topic"}
)
@Import({KafkaAutoConfiguration.class, BookRatingProducerTest.ObjectMapperTestConfig.class})
@Testcontainers
class BookRatingProducerTest {
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
  BookRatingProducer producer;
  @Autowired
  ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws Exception {
    assertDoesNotThrow(() -> producer.requestBookRating(new BookRatingRequestDTO(
        1l,
        "meow",
        "woof",
        "animal sound")));

    KafkaTestConsumer consumer = new KafkaTestConsumer(KAFKA.getBootstrapServers(), "some-group-id");
    consumer.subscribe(List.of("test-topic"));

    ConsumerRecords<String, String> records = consumer.poll();
    assertEquals(1, records.count());
    records.iterator().forEachRemaining(
        record -> {
          try {
            BookRatingRequestDTO message = objectMapper.readValue(record.value(), BookRatingRequestDTO.class);
            assertEquals(1l, message.bookId());
            assertEquals("meow", message.title());
            assertEquals("woof", message.authorFirstName());
            assertEquals("animal sound", message.authorLastName());
          } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
          }
        }
    );
  }
}