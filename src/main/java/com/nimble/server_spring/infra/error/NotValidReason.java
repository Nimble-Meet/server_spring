package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.springframework.validation.FieldError;

public class NotValidReason implements ErrorResponseSource {

    private final Map<String, BadRequestInfo> fieldMap;

    private NotValidReason(Map<String, BadRequestInfo> fieldMap) {
        this.fieldMap = fieldMap;
    }

    public static NotValidReason create(List<FieldError> fieldErrors) {
        Map<String, BadRequestInfo> fieldMap = fieldErrors.stream()
            .map(FieldErrorWrapper::create)
            .collect(
                Collectors.toMap(
                    FieldErrorWrapper::getFieldName,
                    fieldErrorWrapper -> new BadRequestInfo(
                        BadRequestType.NOT_VALID,
                        fieldErrorWrapper.getMessage(),
                        fieldErrorWrapper.getReceivedValue()
                    )
                )
            );
        return new NotValidReason(fieldMap);
    }

    @SneakyThrows
    public String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(fieldMap);
    }

    public ErrorResponse toErrorResponse(ObjectMapper objectMapper) {
        return ErrorResponse.createBadRequestResponse(toJsonString(objectMapper));
    }
}
