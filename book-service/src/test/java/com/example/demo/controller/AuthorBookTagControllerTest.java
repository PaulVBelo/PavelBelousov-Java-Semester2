package com.example.demo.controller;

import com.example.demo.models.authors.Author;
import com.example.demo.models.authors.AuthorRepository;
import com.example.demo.models.authors.records.AuthorResponseDTO;
import com.example.demo.models.books.Book;
import com.example.demo.models.books.BookRepository;
import com.example.demo.models.books.records.BookCreateDTO;
import com.example.demo.models.books.records.BookResponseDTO;
import com.example.demo.models.books.records.BookUpdateDTO;
import com.example.demo.models.tags.Tag;
import com.example.demo.models.tags.TagRepository;
import com.example.demo.models.tags.records.TagResponseDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class AuthorBookTagControllerTest {
  @Container
  @ServiceConnection
  static PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:latest");

  @Autowired
  TestRestTemplate rest;

  @BeforeAll
  static void databaseOn() {
    POSTGRES.start();
  }

  @AfterAll
  static void databaseOff() {
    POSTGRES.stop();
  }

  @Autowired
  AuthorRepository authorRepository;

  @Autowired
  BookRepository bookRepository;

  @Autowired
  TagRepository tagRepository;

  @BeforeEach
  void setUp() {
    tagRepository.deleteAll();
    authorRepository.deleteAll();
    bookRepository.deleteAll();
  }


  @Test
  public void tagTests() {
    String debugName = "fur";
    String debugAlt = "eye";
    String debugEmpty = "";

    //POST

    ResponseEntity<TagResponseDTO> response1 =
        rest.postForEntity("/api/tags", new Tag(debugName), TagResponseDTO.class);
    Assertions.assertTrue(response1.getStatusCode().is2xxSuccessful());
    TagResponseDTO response1Body = response1.getBody();
    Assertions.assertEquals(debugName, response1Body.name());

    //Invalid Requests

    ResponseEntity<TagResponseDTO> response2 =
        rest.postForEntity("/api/tags", new Tag(debugName), TagResponseDTO.class);
    Assertions.assertTrue((response2.getStatusCode().is4xxClientError()));

    ResponseEntity<TagResponseDTO> response3 =
        rest.postForEntity("/api/tags", new Tag(debugEmpty), TagResponseDTO.class);
    Assertions.assertTrue((response3.getStatusCode().is4xxClientError()));

    // PUT

    rest.put("/api/tags/{id}", new Tag(debugAlt), Map.of("id", response1Body.id()));
    rest.put("/api/tags/{id}", new Tag(debugEmpty), Map.of("id", response1Body.id()));

    Tag updated = tagRepository.findById(response1Body.id()).orElseThrow();
    Assertions.assertEquals(debugAlt, updated.getName());

    //DELETE

    rest.delete("/api/tags/{id}", Map.of("id", response1Body.id()));

    Assertions.assertTrue(tagRepository.findById(response1Body.id()).isEmpty());
  }

  @Test
  public void authorTests() {
    String debugFN = "stuart";
    String debugLN = "ave";
    String debugEmpty = "";
    String debugNextFN = "james";
    String debugNextLN = "rusty";

    // POST
    ResponseEntity<AuthorResponseDTO> response1 =
        rest.postForEntity("/api/authors", new Author(debugFN, debugLN), AuthorResponseDTO.class);
    Assertions.assertTrue(response1.getStatusCode().is2xxSuccessful());
    AuthorResponseDTO response1Body = response1.getBody();
    Assertions.assertEquals(debugFN, response1Body.firstName());
    Assertions.assertEquals(debugLN, response1Body.lastName());

    // Invalid Requests

    ResponseEntity<AuthorResponseDTO> response2 =
        rest.postForEntity("/api/authors", new Author(debugEmpty, debugEmpty), AuthorResponseDTO.class);
    Assertions.assertTrue(response2.getStatusCode().is4xxClientError());

    // PUT

    rest.put("/api/authors/{id}", new Author(debugNextFN, debugNextLN), Map.of("id", response1Body.id()));
    rest.put("/api/authors/{id}", new Author(debugEmpty, debugEmpty), Map.of("id", response1Body.id()));

    Author updated = authorRepository.findById(response1Body.id()).orElseThrow();
    Assertions.assertEquals(debugNextFN, updated.getFirstName());
    Assertions.assertEquals(debugNextLN, updated.getLastName());

    //DELETE

    rest.delete("/api/authors/{id}", Map.of("id", response1Body.id()));

    Assertions.assertTrue(authorRepository.findById(response1Body.id()).isEmpty());
  }

  @Test
  public void createBookTest() {
    String debugFN = "stuart";
    String debugLN = "ave";
    String debugTag1 = "fur";
    String debugTag2 = "eye";
    List<String> tags = new ArrayList<>();
    tags.add(debugTag1);
    tags.add(debugTag2);
    String debugTitle = "uno";
    String debugEmpty = "";

    Author author = authorRepository.save(new Author(debugFN, debugLN));
    tagRepository.save(new Tag(debugTag1));

    ResponseEntity<BookResponseDTO> response1 =
        rest.postForEntity("/api/books",
            new BookCreateDTO(debugTitle, author.getId(), tags),
            BookResponseDTO.class);
    Assertions.assertTrue(response1.getStatusCode().is2xxSuccessful());

    BookResponseDTO response1Body = response1.getBody();

    Assertions.assertEquals(author.getId(), response1Body.author().id());
    Assertions.assertEquals(debugTitle, response1Body.title());
    Assertions.assertTrue(response1Body.tags().size()==2 &&
        response1Body.tags().contains(debugTag1) && response1Body.tags().contains(debugTag2));

    // Invalid
    ResponseEntity<BookResponseDTO> response2 =
        rest.postForEntity("/api/books",
            new BookCreateDTO(debugEmpty, author.getId(), tags),
            BookResponseDTO.class);
    Assertions.assertTrue(response2.getStatusCode().is4xxClientError());

    ResponseEntity<BookResponseDTO> response3 =
        rest.postForEntity("/api/books",
            new BookCreateDTO(debugTitle, 10000l, tags),
            BookResponseDTO.class);
    Assertions.assertTrue(response3.getStatusCode().is4xxClientError());

    ResponseEntity<BookResponseDTO> response4 =
        rest.postForEntity("/api/books",
            new BookCreateDTO(debugTitle, author.getId(), null),
            BookResponseDTO.class);
    Assertions.assertTrue(response4.getStatusCode().is4xxClientError());
  }

  @Test
  public void updateAndDeleteBookTest() {
    String debugFN = "stuart";
    String debugLN = "ave";
    String debugTag1 = "fur";
    String debugTag2 = "eye";
    String debugTag3 = "now";
    List<String> tags = new ArrayList<>();
    tags.add(debugTag1);
    tags.add(debugTag2);
    String debugTitle = "uno";
    String debugAltTitle = "dos";
    String debugEmpty = "";

    Author author = authorRepository.save(new Author(debugFN, debugLN));
    Author authorNext = authorRepository.save(new Author(debugLN, debugFN));

    ResponseEntity<BookResponseDTO> response1 =
        rest.postForEntity("/api/books",
            new BookCreateDTO(debugTitle, author.getId(), tags),
            BookResponseDTO.class);
    Assertions.assertTrue(response1.getStatusCode().is2xxSuccessful());

    BookResponseDTO response1Body = response1.getBody();

    tags.remove(1);
    tags.add(debugTag3);

    // PUT

    rest.put("/api/books/{id}",
        new BookUpdateDTO(debugAltTitle, authorNext.getId(), tags),
        Map.of("id", response1Body.id()));

    // Invalid
    rest.put("/api/books/{id}",
        new BookUpdateDTO(debugTitle, 10000l, tags),
        Map.of("id", response1Body.id()));

    rest.put("/api/books/{id}",
        new BookUpdateDTO("", authorNext.getId(), tags),
        Map.of("id", response1Body.id()));

    rest.put("/api/books/{id}",
        new BookUpdateDTO(null, authorNext.getId(), tags),
        Map.of("id", response1Body.id()));

    rest.put("/api/books/{id}",
        new BookUpdateDTO(debugAltTitle, authorNext.getId(), null),
        Map.of("id", response1Body.id()));

    Book bookUpd = bookRepository.findById(response1Body.id()).orElseThrow();
    Author currAuthor = bookUpd.getAuthor();
    List<String> currTags = bookUpd.getTags().stream()
            .map(t -> t.getName()).toList();

    Assertions.assertEquals(debugAltTitle, bookUpd.getTitle());
    Assertions.assertTrue(bookUpd.getTags().size()==2 &&
        currTags.contains(debugTag1) && currTags.contains(debugTag3));
    Assertions.assertEquals(authorNext.getId(), currAuthor.getId());

    // DELETE
    // Will test ALL cascade deleting there
    Tag tag = tagRepository.findByName(debugTag1).orElseThrow();
    rest.delete("/api/tags/{id}", Map.of("id", tag.getId()));
    Book bookNoTag1 = bookRepository.findById(bookUpd.getId()).orElseThrow();
    Assertions.assertTrue(bookNoTag1.getTags().stream()
        .filter(t -> t.getId()==tag.getId()).findFirst().isEmpty());

    ArrayList<String> tagz = new ArrayList<>();
    tagz.add(debugTag2);
    ResponseEntity<BookResponseDTO> response2 =
        rest.postForEntity("/api/books",
            new BookCreateDTO(debugTitle, author.getId(), tagz),
            BookResponseDTO.class);
    Assertions.assertTrue(response2.getStatusCode().is2xxSuccessful());

    BookResponseDTO bookToDelete = response2.getBody();
    rest.delete("/api/books/{id}", Map.of("id", bookToDelete.id()));
    Assertions.assertTrue(bookRepository.findById(bookToDelete.id()).isEmpty());
    TagResponseDTO checkTag1 = rest.getForEntity(
        "/api/tags/{id}", TagResponseDTO.class, Map.of("id",
            tagRepository.findByName(debugTag2).get().getId())
    ).getBody();
    Assertions.assertFalse(checkTag1.books().stream().map(b -> b.id()).toList().contains(bookToDelete.id()));

    rest.delete("/api/authors/{id}", Map.of("id", currAuthor.getId()));
    Assertions.assertTrue(authorRepository.findById(currAuthor.getId()).isEmpty());
    Assertions.assertTrue(bookRepository.findById(bookNoTag1.getId()).isEmpty());
    TagResponseDTO checkTag2 = rest.getForEntity(
        "/api/tags/{id}", TagResponseDTO.class, Map.of("id",
            tagRepository.findByName(debugTag3).get().getId())
    ).getBody();
    Assertions.assertFalse(checkTag2.books().stream().map(b -> b.id()).toList().contains(bookNoTag1.getId()));
  }
}