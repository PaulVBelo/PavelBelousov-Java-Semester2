package com.example.demo.purchase;

public class PurchaseBookException extends RuntimeException{
  public PurchaseBookException(String message, Throwable cause) {
    super(message, cause);
  }
}
