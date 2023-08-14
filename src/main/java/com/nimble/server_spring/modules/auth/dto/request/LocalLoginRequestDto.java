package com.nimble.server_spring.modules.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Validated
@ParameterObject
public class LocalLoginRequestDto {
    private String email;
    private String password;
}
