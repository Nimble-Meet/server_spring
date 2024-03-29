package com.nimble.server_spring.infra.apidoc;

import static java.util.stream.Collectors.groupingBy;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.response.ApiResponseDto;
import com.nimble.server_spring.infra.response.ErrorData;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.models.GroupedOpenApi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
public class SwaggerConfig {

    public static final String JWT_ACCESS_TOKEN = "JwtAccessToken";

    @Bean
    public GroupedOpenApi publicApi() {
        SecurityScheme bearerSecurityScheme = new SecurityScheme()
            .name(JWT_ACCESS_TOKEN)
            .type(Type.HTTP)
            .scheme("Bearer");

        return GroupedOpenApi.builder()
            .group("public")
            .pathsToMatch("/api/**")
            .addOpenApiCustomizer(
                openApi -> openApi.info(new Info().title("Nimble Meet API Docs").version("0.0.1")))
            // Bearer Auth 설정 버튼 생성
            .addOpenApiCustomizer(
                openApi -> openApi.components(openApi.getComponents().addSecuritySchemes(
                    JWT_ACCESS_TOKEN,
                    bearerSecurityScheme
                )))
            .addOperationCustomizer(customize())
            .build();
    }

    public OperationCustomizer customize() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            ApiErrorCodes apiErrorCodes = handlerMethod.getMethodAnnotation(
                ApiErrorCodes.class);
            if (apiErrorCodes != null) {
                generateErrorCodeResponses(operation, apiErrorCodes.value());
            }
            return operation;
        };
    }

    private void generateErrorCodeResponses(Operation operation, ErrorCode[] errorCodes) {
        ApiResponses responses = operation.getResponses();

        Map<Integer, List<ApiExampleHolder>> statusWithExampleHolders =
            Arrays.stream(errorCodes)
                .map(
                    errorCode -> {
                        ApiResponseDto<ErrorData> apiResponse = errorCode.toApiResponse();
                        Example example = new Example();
                        example.description(apiResponse.getData().getMessage());
                        example.value(apiResponse);
                        return ApiExampleHolder.builder()
                            .holder(example)
                            .code(apiResponse.getData().getErrorCode())
                            .statusCode(apiResponse.getStatusCode())
                            .build();
                    })
                .collect(groupingBy(ApiExampleHolder::getStatusCode));
        addExamplesToResponses(responses, statusWithExampleHolders);
    }

    private void addExamplesToResponses(
        ApiResponses responses, Map<Integer, List<ApiExampleHolder>> statusWithExampleHolders
    ) {
        statusWithExampleHolders.forEach(
            (status, exampleHolders) -> {
                Content content = new Content();
                MediaType mediaType = new MediaType();
                ApiResponse apiResponse = new ApiResponse();
                exampleHolders.forEach(
                    exampleHolder -> mediaType.addExamples(
                        exampleHolder.getCode(), exampleHolder.getHolder()));
                content.addMediaType("application/json", mediaType);
                apiResponse.setContent(content);
                responses.addApiResponse(status.toString(), apiResponse);
            });
    }
}
