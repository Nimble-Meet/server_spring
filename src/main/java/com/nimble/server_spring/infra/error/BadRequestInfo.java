package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BadRequestInfo(
    @JsonProperty("errorType") BadRequestType errorType,
    @JsonProperty("requiredType") String requiredType,
    @JsonProperty("receivedValue") String receivedValue
) {

}
