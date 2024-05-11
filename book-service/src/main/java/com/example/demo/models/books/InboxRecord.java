package com.example.demo.models.books;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

@Table(name = "inbox")
@Entity
public class InboxRecord {
  @Id
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "data")
  private String data;

  protected InboxRecord() {}

  public InboxRecord(Long id, String data) {
    this.id = id;
    this.data = data;
  }

  public Long getId() {
    return id;
  }

  public String getData() {
    return data;
  }
}
