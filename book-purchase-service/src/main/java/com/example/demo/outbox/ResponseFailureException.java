package com.example.demo.outbox;

public class ResponseFailureException extends RuntimeException{
  public ResponseFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
