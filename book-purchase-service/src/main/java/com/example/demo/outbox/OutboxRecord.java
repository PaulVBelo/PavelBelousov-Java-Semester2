package com.example.demo.outbox;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Table(name = "outbox")
@Entity
public class OutboxRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull
  private String data;

  protected OutboxRecord() {}

  public Long getId() {
    return id;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public OutboxRecord(String data) {
    this.data = data;
  }
}
