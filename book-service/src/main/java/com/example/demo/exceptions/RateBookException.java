package com.example.demo.exceptions;

public class RateBookException extends RuntimeException {
  public RateBookException(String message, Throwable e) {
    super(message, e);
  }
}
