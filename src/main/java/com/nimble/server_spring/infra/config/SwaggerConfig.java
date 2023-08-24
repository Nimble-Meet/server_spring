package com.nimble.server_spring.infra.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  public static final String JWT_ACCESS_TOKEN = "JwtAccessToken";

  @Bean
  public GroupedOpenApi publicApi() {
    SecurityScheme bearerSecurityScheme = new SecurityScheme()
        .name(JWT_ACCESS_TOKEN)
        .type(Type.HTTP)
        .scheme("Bearer");
    Components bearerAuthComponents = new Components().addSecuritySchemes(
        JWT_ACCESS_TOKEN,
        bearerSecurityScheme
    );

    return GroupedOpenApi.builder()
        .group("public")
        .pathsToMatch("/api/**")
        .addOpenApiCustomizer(
            openApi -> openApi.info(new Info().title("Nimble Meet API Docs").version("0.0.1")))
        // Bearer Auth 설정 버튼 생성
        .addOpenApiCustomizer(openApi -> openApi.components(bearerAuthComponents))
        .build();
  }
}
