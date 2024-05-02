package com.example.demo.history;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Table(name = "history")
@Entity
public class HistoryRecord {
  // Я хотел избежать этого, но придётся вводить ненужный первичный ключ.
  // Если поставить Sheduler на очистку истории каждый день-неделю, то будет работать терпимо.
  // С этим я ничего хорошего увы, не сделаю.
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "request_id")
  private Long requestId;

  @NotNull
  @Column(name = "book_id")
  private Long bookId;

  @NotNull
  @Column(name = "book_title")
  private String title;

  @NotNull
  @Column(name = "author_first_name")
  private String authorFirstName;

  @NotNull
  @Column(name = "author_last_name")
  private String authorLastName;

  @Column(name = "success")
  private Boolean success;

  protected HistoryRecord() {}

  public HistoryRecord(Long requestId,
                       Long bookId,
                       String bookTitle,
                       String authorFirstName,
                       String authorLastName) {
    this.requestId = requestId;
    this.bookId = bookId;
    this.title = bookTitle;
    this.authorFirstName = authorFirstName;
    this.authorLastName = authorLastName;
  }

  public Long getId() {
    return id;
  }

  public Long getBookId() {
    return bookId;
  }

  public String getTitle() {
    return title;
  }

  public String getAuthorFirstName() {
    return authorFirstName;
  }

  public String getAuthorLastName() {
    return authorLastName;
  }

  public Boolean getSuccess() {
    return success;
  }

  public void setSuccess(Boolean success) {
    this.success = success;
  }

  public Long getRequestId() {
    return requestId;
  }
}
