package com.example.demo.purchase;

public class ProhibitedPurchaseException extends RuntimeException{
  public ProhibitedPurchaseException(String message) {
    super(message);
  }
}
