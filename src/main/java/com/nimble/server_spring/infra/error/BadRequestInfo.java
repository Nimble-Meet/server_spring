package com.nimble.server_spring.infra.error;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BadRequestInfo(
    @JsonProperty("errorType") BadRequestType errorType,
    @JsonProperty("required") String required,
    @JsonProperty("receivedValue") String receivedValue
) {

}
