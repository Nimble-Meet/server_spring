package com.nimble.server_spring.infra.response;

import com.nimble.server_spring.infra.error.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public class ErrorData {

    private final String errorCode;
    private final String message;

    @Builder
    private ErrorData(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ErrorData create(HttpStatus httpStatus, String message) {
        return ErrorData.builder()
            .errorCode(httpStatus.name())
            .message(message)
            .build();
    }
}