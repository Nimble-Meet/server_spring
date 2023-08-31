package com.nimble.server_spring.modules.meet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Validated
@ParameterObject
public class MeetCreateRequestDto {
    private String meetName;
    private String description;
}
