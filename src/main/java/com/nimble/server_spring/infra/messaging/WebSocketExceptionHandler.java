package com.nimble.server_spring.infra.messaging;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.response.ApiResponseDto;
import com.nimble.server_spring.infra.response.ErrorData;
import com.nimble.server_spring.infra.response.NotValidReason;
import com.nimble.server_spring.infra.response.TypeMismatchReason;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketExceptionHandler {

    private final ObjectMapper objectMapper;

    public ApiResponseDto<ErrorData> handleErrorCodeException(ErrorCodeException e) {
        return e.getErrorCode().toApiResponse();
    }

    public ApiResponseDto<ErrorData> handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex
    ) {
        BindingResult bindingResult = ex.getBindingResult();
        if (Objects.isNull(bindingResult)) {
            log.error("MethodArgumentNotValidException 이 발생했지만, bindingResult 가 null 입니다.");
            return ApiResponseDto.internalServerError();
        }

        if (!Objects.isNull(bindingResult.getGlobalError())) {
            return ApiResponseDto.badRequest("payload가 null이거나 적절한 형식이 아닙니다.");
        }

        return NotValidReason.create(bindingResult.getFieldErrors())
            .toApiResponse(objectMapper);
    }

    public ApiResponseDto<ErrorData> handleInvalidFormatException(
        InvalidFormatException invalidFormatException
    ) {
        String fieldName = invalidFormatException.getPath().stream()
            .findFirst()
            .map(Reference::getFieldName)
            .orElse(null);
        TypeMismatchReason typeMismatchReason = TypeMismatchReason.create(
            fieldName,
            invalidFormatException.getTargetType(),
            invalidFormatException.getValue()
        );
        return typeMismatchReason.toApiResponse(objectMapper);
    }

    public ApiResponseDto<ErrorData> handleUnknownException(Throwable exception) {
        log.error("STOMP Web Socket의 채팅 요청 처리 중 예상치 못한 예외가 발생했습니다.", exception);
        return ErrorCode.INTERNAL_SERVER_ERROR.toApiResponse();
    }

}
