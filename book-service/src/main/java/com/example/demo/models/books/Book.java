package com.example.demo.models.books;

import java.util.Set;

public class Book {
  private Long id;
  private String title;
  private String author;
  private Set<String> tags;

  public Book(Long id, String title, String author, Set<String> tags) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.tags = tags;
  }

  public Book(Book book) {
    new Book(book.id, book.title, book.author, book.tags);
  }

  public Long getId() {
    return this.id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return this.author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public Set<String> getTags() {
    return this.tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }
}

