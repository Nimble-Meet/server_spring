package com.nimble.server_spring.modules.meet.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.validation.annotation.Validated;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@ParameterObject
public class MeetInviteRequestDto {
    @Email
    @NotNull
    private String email;
}
