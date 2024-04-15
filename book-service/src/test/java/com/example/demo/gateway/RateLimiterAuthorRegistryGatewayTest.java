package com.example.demo.gateway;

import com.example.demo.exceptions.ValidationException;
import com.example.demo.gateway.records.ValidationRequestDTO;
import com.example.demo.gateway.records.ValidationResponseDTO;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
    classes = {AuthorRegistryGatewayImpl.class},
    properties = {
        "resilience4j.ratelimiter.instances.validateBook.limitForPeriod=1",
        "resilience4j.ratelimiter.instances.validateBook.limitRefreshPeriod=1h",
        "resilience4j.ratelimiter.instances.validateBook.timeoutDuration=0"
    }
)
@Import(RateLimiterAutoConfiguration.class)
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RateLimiterAuthorRegistryGatewayTest {
  @Autowired
  private AuthorRegistryGateway gateway;
  @MockBean
  private RestTemplate rest;

  @Test
  void shouldRejectRequestAfterFirstServerSlowResponse() {
    when(rest.exchange(
        eq("/api/registry/validation"),
        eq(HttpMethod.POST),
        any(HttpEntity.class),
        any(ParameterizedTypeReference.class))
    ).thenAnswer((Answer<ResponseEntity<ValidationResponseDTO>>) invocation -> {
      Thread.sleep(2000);
      return new ResponseEntity<>(new ValidationResponseDTO(true), HttpStatus.OK);
    });

    assertDoesNotThrow(
        () -> gateway.validateBook(new ValidationRequestDTO("Sun", "Tzu", "The Art of War"), "1V")
    );

    assertThrows(
        RequestNotPermitted.class,
        () -> gateway.validateBook(new ValidationRequestDTO("Sun", "Tzu", "The Art of War"), "2E")
    );
  }
}
