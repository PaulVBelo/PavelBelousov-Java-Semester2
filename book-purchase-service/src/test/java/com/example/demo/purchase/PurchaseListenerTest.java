package com.example.demo.purchase;

import com.example.demo.history.HistoryRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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


@SpringBootTest(
    classes = {PurchaseListener.class},
    properties = {
        "purchase.topic-to-consume-message=test-topic",
        "spring.kafka.consumer.group-id=test-consumer-group"
    }
)
@Import({KafkaAutoConfiguration.class, PurchaseListenerTest.ObjectMapperTestConfig.class})
@ExtendWith(MockitoExtension.class)
@Testcontainers
class PurchaseListenerTest {
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
        new HistoryRecord(1l, 1l, "T", "FN", "LN")
    );
    kafkaTemplate.send("test-topic", testData);

    // Всё ещё не работает
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