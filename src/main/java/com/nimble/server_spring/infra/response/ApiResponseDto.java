package com.nimble.server_spring.infra.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponseDto<T> {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String statusMessage;
    private final T data;

    @Builder
    private ApiResponseDto(int statusCode, String statusMessage, T data) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.data = data;
    }

    public static <T> ApiResponseDto<T> create(HttpStatus httpStatus, T data) {
        return ApiResponseDto.<T>builder()
            .statusCode(httpStatus.value())
            .statusMessage(httpStatus.name())
            .data(data)
            .build();
    }

    public static <T> ApiResponseDto<T> ok(T data) {
        return ApiResponseDto.create(HttpStatus.OK, data);
    }

    public static ApiResponseDto<ErrorData> error(HttpStatus httpStatus, String message) {
        return ApiResponseDto.create(
            httpStatus,
            ErrorData.create(httpStatus, message)
        );
    }

    public static ApiResponseDto<ErrorData> badRequest(String message) {
        return ApiResponseDto.error(HttpStatus.BAD_REQUEST, message);
    }

    public static ApiResponseDto<ErrorData> internalServerError() {
        return ApiResponseDto.error(HttpStatus.INTERNAL_SERVER_ERROR, "예상치 못한 예외가 발생했습니다.");
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }

    @SneakyThrows
    public byte[] toJsonByteArray(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsBytes(this);
    }
}
