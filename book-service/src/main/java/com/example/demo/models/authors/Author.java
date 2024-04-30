package com.example.demo.models.authors;

import com.example.demo.models.books.Book;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;

@NamedEntityGraph(
    name="graph.authorBooks",
    attributeNodes = @NamedAttributeNode("books")
)
@Entity
@Table(name = "authors")
public class Author {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "first_name")
  @NotBlank(message = "Author firstName has to be filled")
  private String firstName;

  @Column(name = "last_name")
  @NotBlank(message = "Author lastName has to be filled")
  private String lastName;

  @OneToMany(mappedBy = "author", orphanRemoval = true, cascade = PERSIST)
  private List<Book> books = new ArrayList<>();

  protected Author() {}

  public Author(String firstName, String lastName) {
    this.firstName = firstName;
    this.lastName = lastName;
  }

  public Long getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public List<Book> getBooks() {
    return books;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
}
