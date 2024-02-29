package com.nimble.server_spring.infra.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;

public class BadRequestReason {

    protected final Map<String, BadRequestInfo> fieldMap;

    protected BadRequestReason(Map<String, BadRequestInfo> fieldMap) {
        this.fieldMap = fieldMap;
    }

    @SneakyThrows
    private String toJsonString(ObjectMapper objectMapper) {
        return objectMapper.writeValueAsString(fieldMap);
    }

    public ApiResponseDto<ErrorData> toApiResponse(ObjectMapper objectMapper) {
        return ApiResponseDto.badRequest(toJsonString(objectMapper));
    }
}
