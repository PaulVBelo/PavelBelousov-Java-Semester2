package com.example.demo3.rating;

public class RateBookException extends RuntimeException {
  public RateBookException(String message, Throwable e) {
    super(message, e);
  }
}
