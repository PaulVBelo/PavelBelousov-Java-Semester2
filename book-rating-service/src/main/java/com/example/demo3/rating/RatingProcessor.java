package com.example.demo3.rating;

import com.example.demo3.rating.records.BookRatingRequestDTO;
import com.example.demo3.rating.records.BookRatingResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

@Component
public class RatingProcessor implements MessageProcessor{
  private Set<RatedBook> ratedBooks;
  private ObjectMapper objectMapper;
  private RatingServiceProducer producer;

  public RatingProcessor(ObjectMapper objectMapper, RatingServiceProducer producer) {
    this.ratedBooks = new HashSet<>();
    ratedBooks.add(new RatedBook("A diary on information theory", "Alfred", "Renyi", 9));
    ratedBooks.add(new RatedBook("Dialogues on Mathematics", "Alfred", "Renyi", 10));
    ratedBooks.add(new RatedBook("Letters on Probability", "Alfred", "Renyi", 10));
    ratedBooks.add(new RatedBook("Urban Dynamics", "Jay", "Forrester", 8));
    ratedBooks.add(new RatedBook("Industrial Dynamics", "Jay", "Forrester", 7));
    ratedBooks.add(new RatedBook("World Dynamics", "Jay", "Forrester", 9));
    ratedBooks.add(new RatedBook("The Chasm Ahead", "Aurelio", "Peccei", 10));
    ratedBooks.add(new RatedBook("One Hundred Pages for the Future", "Aurelio", "Peccei", 9));
    ratedBooks.add(new RatedBook("Globalization and Its Discontents", "Joseph", "Stiglitz", 8));
    ratedBooks.add(new RatedBook("The Art of War", "Sun", "Tzu", 10));
    ratedBooks.add(new RatedBook("The Electron Microscope", "Dennis", "Gabor", 9));
    ratedBooks.add(new RatedBook("Dynamics of Growth in a Finite World", "Dennis", "Meadows", 10));
    this.objectMapper = objectMapper;
    this.producer = producer;
  }

  @Override
  public void process(String message) {
    try {
      BookRatingRequestDTO bookToRate = objectMapper.readValue(message, BookRatingRequestDTO.class);
      int rating = ratedBooks.stream().filter(b -> b.title().equals(bookToRate.title()) &&
          b.authorFirstName().equals(bookToRate.authorFirstName()) &&
          b.authorLastName().equals(bookToRate.authorLastName())
      ).findFirst().map(b -> b.rating()).orElseThrow();
      BookRatingResponseDTO responseDTO = new BookRatingResponseDTO(bookToRate.bookId(), rating);
      producer.sendRatingResponse(responseDTO);
    } catch (JsonProcessingException e) {
    }catch (NoSuchElementException e) {
      // Не должно произойти. В худшем случае просто не отвечаем.
    }
  }
}
