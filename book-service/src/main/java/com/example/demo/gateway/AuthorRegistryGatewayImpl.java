package com.example.demo.gateway;

import com.example.demo.exceptions.ValidationException;
import com.example.demo.gateway.records.ValidationRequestDTO;
import com.example.demo.gateway.records.ValidationResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service("authorRegistryService")
@ConditionalOnProperty(value = "author-registry-gateway.mode", havingValue = "http")
public class AuthorRegistryGatewayImpl implements AuthorRegistryGateway {
  private final RestTemplate restTemplate;

  @Autowired
  public AuthorRegistryGatewayImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @RateLimiter(name = "validateBook")
  @CircuitBreaker(name = "validateBook")
  @Retry(name = "validateBookRetry")
  public ValidationResponseDTO validateBook(ValidationRequestDTO bookToValidate, String requestId) {
    try {
      HttpHeaders headers = new HttpHeaders();
      headers.add("X-REQUEST-ID", requestId);
      ResponseEntity<ValidationResponseDTO> validationResponse =
          restTemplate.exchange(
              "/api/registry/validation",
              HttpMethod.POST,
              new HttpEntity<>(
                  Map.of(
                      "firstName",
                          bookToValidate.firstName(),
                      "lastName",
                          bookToValidate.lastName(),
                      "title",
                          bookToValidate.title()
                  )
                  , headers),
              new ParameterizedTypeReference<>() {
              }
          );
      return validationResponse.getBody();
    } catch (RestClientException e) {
      throw new ValidationException("Error during requesting author-registry service: " + e.getMessage());
    }
  }
}
