package com.example.demo.controller;

import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.books.records.BookCreateDTO;
import com.example.demo.models.books.records.BookDTO;
import com.example.demo.models.books.records.BookUpdateDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookControllerTest {
  @Autowired
  private TestRestTemplate rest;
  @Autowired
  private BookRepository bookRepository;

  // Я не нашёл, как десериализовать список книг, поэтому сделаю это для теста по-старинке (см. getBooksByTagTest)
  @Autowired
  private ObjectMapper objectMapper;

  @AfterEach
  public void cleanUp() {
    bookRepository.deleteAll();
  }

  @Test
  public void createBookTest() {
    String debugEmpty = "";
    String debugTitle = "meow";
    String debugAuthor = "cat";
    String debugTag = "fur";
    List<String> debugTags = new ArrayList<>();
    debugTags.add(debugTag);

    ResponseEntity<BookDTO> createBookResponse =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugAuthor, debugTags), BookDTO.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    BookDTO createBookResponseBody = createBookResponse.getBody();

    ResponseEntity<Book> getBookResponse =
        rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", createBookResponseBody.id()));
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());

    Book getBookResponseBody = getBookResponse.getBody();
    assertEquals(createBookResponseBody.id(), getBookResponseBody.getId());
    assertEquals(debugTitle, getBookResponseBody.getTitle());
    assertEquals(debugAuthor, getBookResponseBody.getAuthor());
    assertTrue(getBookResponseBody.getTags().size()==1
        && getBookResponseBody.getTags().contains(debugTag));

    // Invalid requests

    ResponseEntity<BookDTO> invalidCreateBookResponse1 =
        rest.postForEntity("/api/books", new BookCreateDTO(debugEmpty, debugAuthor, debugTags), BookDTO.class);
    assertTrue(invalidCreateBookResponse1.getStatusCode().is4xxClientError(), "Unexpected status code: " + invalidCreateBookResponse1.getStatusCode());

    ResponseEntity<BookDTO> invalidCreateBookResponse2 =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugEmpty, debugTags), BookDTO.class);
    assertTrue(invalidCreateBookResponse2.getStatusCode().is4xxClientError(), "Unexpected status code: " + invalidCreateBookResponse2.getStatusCode());

    ResponseEntity<BookDTO> invalidCreateBookResponse3 =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugAuthor, null), BookDTO.class);
    assertTrue(invalidCreateBookResponse3.getStatusCode().is4xxClientError(), "Unexpected status code: " + invalidCreateBookResponse3.getStatusCode());
  }

  @Test
  public void getBookTest() {
    String debugTitle = "meow";
    String debugAuthor = "cat";
    String debugTag = "fur";
    List<String> debugTags = new ArrayList<>();
    debugTags.add(debugTag);

    ResponseEntity<BookDTO> createBookResponse =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugAuthor, debugTags), BookDTO.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    BookDTO createBookResponseBody = createBookResponse.getBody();

    ResponseEntity<Book> getBookResponse =
        rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", createBookResponseBody.id()));
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());

    Book getBookResponseBody = getBookResponse.getBody();

    assertEquals(createBookResponseBody.id(), getBookResponseBody.getId());
    assertEquals(debugTitle, getBookResponseBody.getTitle());
    assertEquals(debugAuthor, getBookResponseBody.getAuthor());
    assertTrue(getBookResponseBody.getTags().size()==1
        && getBookResponseBody.getTags().contains(debugTag));

    // Invalid Request
    ResponseEntity<Book> invalidGetBookResponse =
        rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", 10000l));
    assertTrue(invalidGetBookResponse.getStatusCode().is4xxClientError(), "Unexpected status code: " + invalidGetBookResponse.getStatusCode());
  }

  @Test
  public void updateBookTest() {
    String debugTitle = "meow";
    String debugAuthor = "cat";
    String debugTag = "fur";
    List<String> debugTags = new ArrayList<>();
    debugTags.add(debugTag);

    String altTitle = "moo";
    String altTag = "gun";
    List<String> altTags = new ArrayList<>();
    altTags.add(altTag);

    ResponseEntity<BookDTO> createBookResponse =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugAuthor, debugTags), BookDTO.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    BookDTO createBookResponseBody = createBookResponse.getBody();

    rest.put("/api/books/{id}", new BookUpdateDTO(altTitle, "", altTags), Map.of("id", createBookResponseBody.id()));

    ResponseEntity<Book> getBookResponse =
        rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", createBookResponseBody.id()));
    assertTrue(getBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBookResponse.getStatusCode());

    Book getBookResponseBody = getBookResponse.getBody();

    assertEquals(altTitle, getBookResponseBody.getTitle());
    assertEquals(debugAuthor, getBookResponseBody.getAuthor());
    assertTrue(getBookResponseBody.getTags().size()==1 && getBookResponseBody.getTags().contains(altTag));

    // Bad Requests
    rest.put("/api/books/{id}", new BookUpdateDTO(null, "", altTags), Map.of("id", createBookResponseBody.id()));
    rest.put("/api/books/{id}", new BookUpdateDTO("", null, altTags), Map.of("id", createBookResponseBody.id()));
    rest.put("/api/books/{id}", new BookUpdateDTO("", "", null), Map.of("id", createBookResponseBody.id()));

    ResponseEntity<Book> getUnchangedBookResponse =
        rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", createBookResponseBody.id()));
    assertTrue(getUnchangedBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getUnchangedBookResponse.getStatusCode());

    Book getUnchangedBookResponseBody = getUnchangedBookResponse.getBody();
    assertEquals(altTitle, getUnchangedBookResponseBody.getTitle());
    assertEquals(debugAuthor, getUnchangedBookResponseBody.getAuthor());
    assertTrue(getUnchangedBookResponseBody.getTags().size()==1 && getUnchangedBookResponseBody.getTags().contains(altTag));
  }

  @Test
  public void deleteBookTest() {
    String debugTitle = "meow";
    String debugAuthor = "cat";
    String debugTag = "fur";
    List<String> debugTags = new ArrayList<>();
    debugTags.add(debugTag);

    ResponseEntity<BookDTO> createBookResponse =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugAuthor, debugTags), BookDTO.class);
    assertTrue(createBookResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse.getStatusCode());
    BookDTO createBookResponseBody = createBookResponse.getBody();

    rest.delete("/api/books/{id}", Map.of("id", createBookResponseBody.id()));

    ResponseEntity<Book> getBookResponse =
        rest.getForEntity("/api/books/{id}", Book.class, Map.of("id", createBookResponseBody.id()));
    assertTrue(getBookResponse.getStatusCode().is4xxClientError(), "Unexpected status code: " + getBookResponse.getStatusCode());
  }

  @Test
  public void getBooksByTagTest() throws Exception{
    String debugTitle = "meow";
    String debugAuthor = "cat";
    String debugTag = "fur";
    List<String> debugTags = new ArrayList<>();
    debugTags.add(debugTag);

    String altTitle = "moo";
    String altTag = "gun";
    List<String> altTags = new ArrayList<>();
    altTags.add(altTag);

    ResponseEntity<BookDTO> createBookResponse1 =
        rest.postForEntity("/api/books", new BookCreateDTO(debugTitle, debugAuthor, debugTags), BookDTO.class);
    assertTrue(createBookResponse1.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse1.getStatusCode());
    BookDTO createBookResponseBody1 = createBookResponse1.getBody();

    ResponseEntity<BookDTO> createBookResponse2 =
        rest.postForEntity("/api/books", new BookCreateDTO(altTitle, debugAuthor, altTags), BookDTO.class);
    assertTrue(createBookResponse2.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + createBookResponse2.getStatusCode());
    BookDTO createBookResponseBody2 = createBookResponse2.getBody();

    ResponseEntity<String> getBooksResponse =
        rest.getForEntity("/api/books/filter?tag={tag}", String.class, Map.of("tag", debugTag));
    assertTrue(getBooksResponse.getStatusCode().is2xxSuccessful(), "Unexpected status code: " + getBooksResponse.getStatusCode());
    List<BookDTO> getBooksResponseBody = objectMapper.readValue(getBooksResponse.getBody(),
        new TypeReference<>() {
          @Override
          public Type getType() {
            return super.getType();
          }
        });

    assertTrue(getBooksResponseBody.stream()
        .filter(bookDTO -> bookDTO.id() == createBookResponseBody1.id())
        .findFirst().isPresent());

    assertTrue(getBooksResponseBody.stream()
        .filter(bookDTO -> bookDTO.id() == createBookResponseBody2.id())
        .findFirst().isEmpty());
  }
}