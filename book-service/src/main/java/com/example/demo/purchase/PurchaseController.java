package com.example.demo.purchase;

import com.example.demo.exceptions.ApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/books/purchase")
public class PurchaseController {
  private final PurchaseService service;

  @Autowired
  public PurchaseController(PurchaseService service) {
    this.service = service;
  }

  @PutMapping("/{id}")
  @Transactional
  public void purchaseBook(@PathVariable("id") Long id) {
    service.prepareBookPurchaseRequest(id);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> noSuchElementExceptionHandler(NoSuchElementException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> prohibitedPurchaseExceptionHandler(ProhibitedPurchaseException e) {
    return new ResponseEntity(new ApiError(e.getMessage()), HttpStatus.BAD_REQUEST);
  }
}
