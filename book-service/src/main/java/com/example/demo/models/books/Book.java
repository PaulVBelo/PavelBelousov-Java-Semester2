package com.example.demo.models.books;

import com.example.demo.models.authors.Author;
import com.example.demo.models.tags.Tag;
import com.example.demo.purchase.ProhibitedPurchaseException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;


import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.FetchType.EAGER;


@Entity
@Table(name = "books")
public class Book {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private Author author;

  @Column(name = "title")
  @NotNull(message = "Book title has to be filled")
  private String title;

  @Column(name = "rating")
  private int rating;

  // Прочитал в интернете. Вроде так тратится больше памяти, но это выглядит удобно, и default = OK имеет смысл.
  // Я хотел использовать String, но показалось, что от меня требуется Enum. Поэтому Enum.
  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToMany(fetch = EAGER, cascade = PERSIST)
  @JoinTable(
      name = "tag_book",
      joinColumns = @JoinColumn(name = "book_id"),
      inverseJoinColumns = @JoinColumn(name = "tag_id")
  )
  private Set<Tag> tags = new HashSet<>();

  protected Book() {}

  public Book(String title, Author author) {
    this.title = title;
    this.author = author;
    this.status = Status.OK;
  }

  public Long getId() {
    return id;
  }

  public Author getAuthor() {
    return author;
  }

  public String getTitle() {
    return title;
  }

  public Set<Tag> getTags() {
    return tags;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setTags(Set<Tag> tags) {
    this.tags = tags;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public void buyBook() {
    switch (this.status) {
      case PAYMENT_PENDING:
        throw new ProhibitedPurchaseException("Book is in the process of payment");
      case BOUGHT:
        throw new ProhibitedPurchaseException("This book has already been bought");
      default:
        this.status = Status.PAYMENT_PENDING;
    }
  }
}

