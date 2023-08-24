package com.nimble.server_spring.infra.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("public")
        .addOperationCustomizer((operation, handlerMethod) -> {
          operation.addSecurityItem(new SecurityRequirement().addList("basicScheme"));
          return operation;
        })
        .addOpenApiCustomizer(
            openApi -> openApi.info(new Info().title("Nimble Meet API Docs").version("0.0.1")))
        .pathsToMatch("/api/**")
        .build();
  }
}
