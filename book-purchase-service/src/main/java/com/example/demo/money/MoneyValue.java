package com.example.demo.money;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Currency;

@Table(name = "money")
@Entity
public class MoneyValue {
  // Реализация такая - есть endpoint на добавление денег. Если денег нет - это будет ячейкой. Если есть - добавляем к значению.
  // Практичней было бы делать проверку в классе-сервисе, ну да ладно. Сделаю так.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "deposit_id")
  private Long id;

  @DecimalMin(value = "0.0", inclusive = false, message = "Naughty-naughty! Why would you want to deposit negative money?")
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Column(name = "amount")
  private BigDecimal amount;

  protected MoneyValue() {}

  public MoneyValue(BigDecimal amount) {
    this.amount = amount;
  }

  public Long getId() {
    return id;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
}
