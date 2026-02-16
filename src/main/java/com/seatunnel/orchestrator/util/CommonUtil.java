package com.seatunnel.orchestrator.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.seatunnel.orchestrator.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommonUtil {

  private final Validator validator;
  private final RestTemplate restTemplate;

  public void validateMethodArguments(Object payload) {
    DataBinder dataBinder = new DataBinder(payload);
    dataBinder.setValidator(this.validator);
    dataBinder.validate();
    BindingResult result = dataBinder.getBindingResult();
    Map<String, List<String>> fieldErrors = result.getFieldErrors().stream()
      .collect(Collectors.groupingBy(
        FieldError::getField,
        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage,
          Collectors.toList())));
    if (!fieldErrors.isEmpty()) {
      throw new ApiException(HttpStatus.BAD_REQUEST, fieldErrors);
    }
  }

  public JsonNode restClient(Object requestBody,
                             HttpMethod httpMethod, String url, HttpHeaders httpHeaders) {
    log.info("------- Making rest call to url: {}, method: {} --------", url, httpMethod);
    if (httpHeaders == null) {
      httpHeaders = new HttpHeaders();
    }
    httpHeaders.add("Content-Type", "application/json");
    HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
    try {
      ResponseEntity<JsonNode> response = restTemplate
        .exchange(url, httpMethod, requestEntity, JsonNode.class);
      log.info("------- Response status code: {} --------", response.getStatusCode());
      return response.getBody();
    } catch (RestClientResponseException e) {
      log.error("-------Failed to make rest call-----------");
      log.error("-------Response status code: {} --------", e.getStatusCode());
      log.error("-------Response body: {} --------", e.getResponseBodyAs(Object.class));
      throw new ApiException(HttpStatus.valueOf(e.getStatusCode().value()),
        e.getResponseBodyAs(Object.class));
    }
  }


}
