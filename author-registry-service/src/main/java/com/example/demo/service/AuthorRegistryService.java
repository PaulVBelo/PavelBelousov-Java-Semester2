package com.example.demo.service;

import com.example.demo.service.models.Author;
import com.example.demo.service.models.Book;
import com.example.demo.service.records.ValidationRequestDTO;
import com.example.demo.service.records.ValidationResponseDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/registry")
@Validated
public class AuthorRegistryService {
  private final Set<Book> validBooks;
  private final HashMap<String, Boolean> completeRequests;

  public AuthorRegistryService() {
    this.completeRequests = new HashMap<>();
    this.validBooks = new HashSet<>();
    validBooks.add(new Book("A diary on information theory", new Author("Alfred", "Renyi")));
    validBooks.add(new Book("Dialogues on Mathematics", new Author("Alfred", "Renyi")));
    validBooks.add(new Book("Letters on Probability", new Author("Alfred", "Renyi")));
    validBooks.add(new Book("Urban Dynamics", new Author("Jay", "Forrester")));
    validBooks.add(new Book("Industrial Dynamics", new Author("Jay", "Forrester")));
    validBooks.add(new Book("World Dynamics", new Author("Jay", "Forrester")));
    validBooks.add(new Book("The Chasm Ahead", new Author("Aurelio", "Peccei")));
    validBooks.add(new Book("One Hundred Pages for the Future", new Author("Aurelio", "Peccei")));
    validBooks.add(new Book("Globalization and Its Discontents", new Author("Joseph", "Stiglitz")));
    validBooks.add(new Book("The Art of War", new Author("Sun", "Tzu")));
    validBooks.add(new Book("The Electron Microscope", new Author("Dennis", "Gabor")));
    validBooks.add(new Book("Dynamics of Growth in a Finite World", new Author("Dennis", "Meadows")));
  }

  @PostMapping("/validation")
  public ValidationResponseDTO validateBook(@RequestBody @Valid ValidationRequestDTO bookToValidate,
                                            @NotNull @RequestHeader("X-REQUEST-ID") String requestId) {
    if (!completeRequests.containsKey(requestId)) {
      boolean result = validBooks.stream().filter(b ->
          b.title().equals(bookToValidate.title()) &&
              b.author().firstName().equals(bookToValidate.firstName()) &&
              b.author().lastName().equals(bookToValidate.lastName())
      ).findFirst().isPresent();
      completeRequests.put(requestId, result);
    }
    return new ValidationResponseDTO(completeRequests.get(requestId));
  }

  public Set<Book> getValidBooks() {
    return validBooks;
  }

  public HashMap<String, Boolean> getCompleteRequests() {
    return completeRequests;
  }
}
