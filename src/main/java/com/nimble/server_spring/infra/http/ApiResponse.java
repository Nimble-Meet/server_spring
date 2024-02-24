package com.nimble.server_spring.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String statusMessage;
    private final T data;

    @Builder
    private ApiResponse(int statusCode, String statusMessage, T data) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
    }

    public static <T> ApiResponse<T> create(HttpStatus httpStatus, T data) {
        return ApiResponse.<T>builder()
            .statusCode(httpStatus.value())
            .statusMessage(httpStatus.name())
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.create(HttpStatus.OK, data);
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }
}
