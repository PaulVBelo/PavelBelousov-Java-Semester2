package com.example.demo.models.exceptions;

public record ApiError(String message) {
  public ApiError(String message) {
    this.message = message;
  }

  public String message() {
    return this.message;
  }
}
