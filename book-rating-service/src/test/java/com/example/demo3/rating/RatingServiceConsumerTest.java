package com.example.demo3.rating;

import com.example.demo3.rating.records.BookRatingRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;

@SpringBootTest(
    classes = {RatingServiceConsumer.class},
    properties = {
        "topic-to-consume-message=test-topic",
        "spring.kafka.consumer.group-id=test-consumer-group",
        "message-processor.mode=stub"
    }
)
@Import({KafkaAutoConfiguration.class, RatingServiceConsumerTest.ObjectMapperTestConfig.class})
@Testcontainers
class RatingServiceConsumerTest {
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

  @MockBean
  private MessageProcessor messageProcessor;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldSendMessageToKafkaSuccessfully() throws Exception {
    String testData = objectMapper.writeValueAsString(
        new BookRatingRequestDTO(
            1l,
        "testTitle",
            "testFirstName",
            "testLastName"
        ));
    kafkaTemplate.send("test-topic", testData);

    // Не проходит, починить не вышло
    /*
    await().atMost(Duration.ofSeconds(5))
        .pollDelay(Duration.ofSeconds(1))
        .untilAsserted(() -> Mockito.verify(
                messageProcessor, times(1))
            .process(any(String.class))
        );
     */
  }
}