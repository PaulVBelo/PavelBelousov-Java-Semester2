package com.example.demo.purchase;

import com.example.demo.history.HistoryRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PurchaseListener {
  private final ObjectMapper objectMapper;
  private final MessageProcessor processor;

  @Autowired
  public PurchaseListener(ObjectMapper objectMapper, MessageProcessor processor) {
    this.objectMapper = objectMapper;
    this.processor = processor;
  }

  @KafkaListener(topics = {"${purchase.topic-to-consume-message}"})
  @Transactional
  public void consumeMessage(String message, Acknowledgment acknowledgment) throws JsonProcessingException {
    var result = objectMapper.readValue(message, HistoryRecord.class);
    processor.process(message);
    acknowledgment.acknowledge();
  }
}
