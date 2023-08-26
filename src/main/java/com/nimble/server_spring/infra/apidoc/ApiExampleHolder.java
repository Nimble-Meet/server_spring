package com.nimble.server_spring.infra.apidoc;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiExampleHolder {

  private Example holder;
  private String code;
  private int statusCode;
}