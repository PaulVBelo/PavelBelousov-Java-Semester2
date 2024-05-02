package com.example.demo.purchase;

import com.example.demo.history.HistoryRecord;
import com.example.demo.history.HistoryRepository;
import com.example.demo.money.MoneyRepository;
import com.example.demo.money.MoneyValue;
import com.example.demo.outbox.OutboxRecord;
import com.example.demo.outbox.OutboxRepository;
import com.example.demo.outbox.PurchaseResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
public class PurchaseProcessor implements MessageProcessor {
  private final ObjectMapper objectMapper;
  private final HistoryRepository historyRepository;
  private final OutboxRepository outboxRepository;
  private final MoneyRepository moneyRepository;

  public PurchaseProcessor(ObjectMapper objectMapper,
                           HistoryRepository historyRepository,
                           OutboxRepository outboxRepository,
                           MoneyRepository moneyRepository) {
    this.objectMapper = objectMapper;
    this.historyRepository = historyRepository;
    this.outboxRepository = outboxRepository;
    this.moneyRepository = moneyRepository;
  }

  @Override
  @Transactional
  public void process(String message) {
    try {
      HistoryRecord resultMessage = objectMapper.readValue(message, HistoryRecord.class);
      Optional<HistoryRecord> checkUnique = historyRepository.findAll().stream()
          .filter(req -> req.getRequestId()==resultMessage.getRequestId()).findFirst();
      if (!checkUnique.isPresent()) {
        // Обойдусь здесь без бросания очередной ошибки. Если дубликат - ничего не будет. Почти идеальный сценарий.
        List<MoneyValue> currMoney = moneyRepository.findAll();
        if (currMoney.size() == 0) {
          resultMessage.setSuccess(false);
          historyRepository.save(resultMessage);
          throw new IllegalStateException("No money has been deposited yet!");
        } else if (currMoney.size() >= 2) {
          resultMessage.setSuccess(false);
          historyRepository.save(resultMessage);
          throw new IllegalStateException("Database was corrupted");
        } else {
          MoneyValue currMoneyVal= currMoney.get(0);
          if (currMoneyVal.getAmount().compareTo(new BigDecimal(100))>=0) {
            currMoneyVal.setAmount(currMoneyVal.getAmount().subtract(new BigDecimal(100)));
            moneyRepository.save(currMoneyVal);
            resultMessage.setSuccess(true);
            historyRepository.save(resultMessage);
            try {
              outboxRepository.save(new OutboxRecord(
                      objectMapper.writeValueAsString(
                          new PurchaseResponse(resultMessage.getId(), resultMessage.getBookId(), resultMessage.getSuccess())
                      )
                  )
              );
            } catch (JsonProcessingException e) {
            }
          } else {
            resultMessage.setSuccess(false);
            historyRepository.save(resultMessage);
            outboxRepository.save(new OutboxRecord(
                    objectMapper.writeValueAsString(
                        new PurchaseResponse(resultMessage.getId(), resultMessage.getBookId(), resultMessage.getSuccess())
                    )
                )
            );
          }
        }
      } else {
        outboxRepository.save(new OutboxRecord(
                objectMapper.writeValueAsString(
                    new PurchaseResponse(checkUnique.get().getId(), resultMessage.getBookId(), checkUnique.get().getSuccess())
                )
            )
        );
      }
    } catch (JsonProcessingException e) {
    }
  }
}
