package com.example.demo.models.tags;

import com.example.demo.models.books.Book;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;
  @Column(name = "name")
  @NotBlank(message = "Tag name has to be filled")
  private String name;

  @ManyToMany(mappedBy = "tags")
  private Set<Book> books = new HashSet<>();

  protected Tag() {}

  public Tag(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Set<Book> getBooks() {
    return books;
  }

  public void setName(String name) {
    this.name = name;
  }
}
