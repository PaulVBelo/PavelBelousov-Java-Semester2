package com.example.demo.gateway;

import com.example.demo.gateway.records.ValidationRequestDTO;
import com.example.demo.gateway.records.ValidationResponseDTO;

public interface AuthorRegistryGateway {
  ValidationResponseDTO validateBook(ValidationRequestDTO bookToValidate, String requestId);
}
