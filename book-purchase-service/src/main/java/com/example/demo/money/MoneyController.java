package com.example.demo.money;


import com.example.demo.ApiError;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/money")
public class MoneyController {
  // Раз уж деньги хранятся в БД, то мне нужно endpoint чтобы их ввести внутрь.
  private final MoneyRepository moneyRepository;

  @Autowired
  public MoneyController(MoneyRepository moneyRepository) {
    this.moneyRepository = moneyRepository;
  }

  @Transactional
  @PostMapping
  public MoneyValue depositMoney(@RequestBody @Valid MoneyValue depositValue) {
    List<MoneyValue> currMoney = moneyRepository.findAll();
    if (currMoney.size() > 0) {
      MoneyValue currValue = currMoney.get(0);
      currValue.setAmount(currValue.getAmount().add(depositValue.getAmount()));
      return moneyRepository.save(currValue);
    } else {
      return moneyRepository.save(depositValue);
    }
  }

  @ExceptionHandler
  public ResponseEntity<ApiError> constraintViolationExceptionResponse(ConstraintViolationException e) {
    return new ResponseEntity(new ApiError(e.getConstraintViolations()
        .stream().map(ConstraintViolation::getMessage)
        .collect(Collectors.joining(", "))),
        HttpStatus.BAD_REQUEST);
  }
}
