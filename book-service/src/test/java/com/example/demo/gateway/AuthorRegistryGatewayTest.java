package com.example.demo.gateway;

import com.example.demo.RestTemplateConfiguration;
import com.example.demo.exceptions.ValidationException;
import com.example.demo.gateway.records.ValidationRequestDTO;
import com.example.demo.gateway.records.ValidationResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockserver.client.server.MockServerClient;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.Delay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.JsonBody.json;

@Testcontainers
@SpringBootTest(classes = {AuthorRegistryGatewayImpl.class, RestTemplateConfiguration.class})
class AuthorRegistryGatewayTest {
  @Container
  public static final MockServerContainer mockServer =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.13.2"));

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("author-registry-service.base.url", mockServer::getEndpoint);
  }

  @Autowired
  private AuthorRegistryGatewayImpl gateway;


  @Test
  void validationTest() {
    var client = new MockServerClient(mockServer.getHost(), mockServer.getServerPort());
    client
        .when(request()
              .withMethod("POST")
              .withPath("/api/registry/validation")
              .withBody(json("{\"firstName\":\"Sun\",\"lastName\":\"Tzu\",\"title\":\"The Art of War\"}", MatchType.ONLY_MATCHING_FIELDS)))
        .respond(
          response()
              .withStatusCode(200)
              .withHeader("Content-Type", "application/json")
              .withBody(json("{\"validationResult\":true}")));

    ValidationResponseDTO validationResponse = gateway.validateBook(new ValidationRequestDTO(
        "Sun", "Tzu", "The Art of War"
        ), "123");

    assertTrue(validationResponse.validationResult());
  }
}