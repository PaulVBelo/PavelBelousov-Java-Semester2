package com.example.demo.models.books;

import com.example.demo.models.books.records.BookCreateDTO;
import com.example.demo.models.books.records.BookDTO;
import com.example.demo.models.books.records.BookUpdateDTO;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class BookRepositoryInMemory implements BookRepository {
  private final AtomicLong nextId = new AtomicLong(0L);
  private final List<Book> books = new CopyOnWriteArrayList();

  public Optional<Book> getBook(Long id) {
    return books.stream()
        .filter(book -> book.getId() == id)
        .findFirst();
  }

  public BookDTO addBook(BookCreateDTO toCreate) {
    Book book = new Book(nextId.incrementAndGet(), toCreate.title(), toCreate.author(), new HashSet(toCreate.tags()));
    books.add(book);
    return new BookDTO(book.getId(), book.getTitle(), book.getAuthor(), new ArrayList(book.getTags()));
  }

  public synchronized void updateBook(Long id, BookUpdateDTO toUpdate) throws NoSuchElementException {
    for (Book b: books) {
      if (b.getId() == id) {
        if (!toUpdate.title().equals("")) {
          b.setTitle(toUpdate.title());
        }
        if (!toUpdate.author().equals("")) {
          b.setAuthor(toUpdate.author());
        }
        if (toUpdate.tags().size() > 0) {
          b.setTags(new HashSet(toUpdate.tags()));
        }
        return;
      }
    }
    throw new NoSuchElementException("Cannot find a book with ID " + id);
  }

  public synchronized void deleteBook(Long id) throws NoSuchElementException {
    for(int i = 0; i < this.books.size(); ++i) {
      if (books.get(i).getId() == id) {
        books.remove(i);
        return;
      }
    }

    throw new NoSuchElementException("Cannot find a book with ID " + id);
  }

  public List<Book> getBooksAll() {
    return new ArrayList(books);
  }

  public synchronized List<Book> getBooksByTag(String tag) {
    return books.stream()
        .filter(book -> book.getTags().contains(tag))
        .collect(Collectors.toList());
  }

  public synchronized void deleteAll() {
    books.clear();
    nextId.set(0L);
  }
}