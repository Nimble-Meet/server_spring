package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ErrorResponseSource {

    public ErrorResponse toErrorResponse(ObjectMapper objectMapper);
}
