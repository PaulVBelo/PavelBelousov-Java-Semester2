package com.example.demo.exceptions;

public record ApiError(String message) {
  public ApiError(String message) {
    this.message = message;
  }

  public String message() {
    return this.message;
  }
}
