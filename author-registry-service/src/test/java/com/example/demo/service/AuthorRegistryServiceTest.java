package com.example.demo.service;

import com.example.demo.service.records.ValidationResponseDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthorRegistryServiceTest {
  @Autowired
  private TestRestTemplate rest;

  @Autowired
  private AuthorRegistryService service;

  @Test
  public void validationTests() {
    String debugTitle = "A diary on information theory";
    String debugFN = "Alfred";
    String debugLN = "Renyi";
    String debugInvalid = "Illegal String";

    String testUUID1 = "c9-05-f6";
    String testUUID2 = "1a-e6-70";
    String testUUID3 = "ee-bf-d3";

    HttpHeaders headers1 = new HttpHeaders();
    headers1.add("X-REQUEST-ID", testUUID1);

    ResponseEntity<ValidationResponseDTO> response1 =
        rest.exchange(
            "/api/registry/validation",
            HttpMethod.POST,
            new HttpEntity<>(
                Map.of(
                    "firstName",
                    debugFN,
                    "lastName",
                    debugLN,
                    "title",
                    debugTitle
                )
                , headers1),
            new ParameterizedTypeReference<ValidationResponseDTO>() {
            }
        );

    Assertions.assertTrue(response1.getStatusCode().is2xxSuccessful());
    Assertions.assertTrue(response1.getBody().validationResult());
    Assertions.assertTrue(service.getCompleteRequests().containsKey(testUUID1));
    Assertions.assertTrue(service.getCompleteRequests().get(testUUID1));

    HttpHeaders headers2 = new HttpHeaders();
    headers2.add("X-REQUEST-ID", testUUID2);

    ResponseEntity<ValidationResponseDTO> response2 =
        rest.exchange(
            "/api/registry/validation",
            HttpMethod.POST,
            new HttpEntity<>(
                Map.of(
                    "firstName",
                    debugFN,
                    "lastName",
                    debugLN,
                    "title",
                    debugInvalid
                )
                , headers2),
            new ParameterizedTypeReference<ValidationResponseDTO>() {
            }
        );

    Assertions.assertTrue(response2.getStatusCode().is2xxSuccessful());
    Assertions.assertFalse(response2.getBody().validationResult());
    Assertions.assertTrue(service.getCompleteRequests().containsKey(testUUID2));
    Assertions.assertFalse(service.getCompleteRequests().get(testUUID2));

    HttpHeaders headers3 = new HttpHeaders();
    headers3.add("X-REQUEST-ID", testUUID3);

    ResponseEntity<ValidationResponseDTO> response3 =
        rest.exchange(
            "/api/registry/validation",
            HttpMethod.POST,
            new HttpEntity<>(
                Map.of(
                    "firstName",
                    debugInvalid,
                    "lastName",
                    debugLN,
                    "title",
                    debugTitle
                )
                , headers3),
            new ParameterizedTypeReference<ValidationResponseDTO>() {
            }
        );

    Assertions.assertTrue(response3.getStatusCode().is2xxSuccessful());
    Assertions.assertFalse(response3.getBody().validationResult());
    Assertions.assertTrue(service.getCompleteRequests().containsKey(testUUID3));
    Assertions.assertFalse(service.getCompleteRequests().get(testUUID3));
  }
}