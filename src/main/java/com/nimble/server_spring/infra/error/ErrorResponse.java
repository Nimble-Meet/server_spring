package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@ToString
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int status;
    private final String error;
    private final String code;
    private final String message;

    public static ErrorResponse createBadRequestResponse(String message) {
        return ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.name())
            .code(HttpStatus.BAD_REQUEST.name())
            .message(message)
            .build();
    }

    @SneakyThrows
    public byte[] toJsonByteArray(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsBytes(this);
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(this);
    }
}