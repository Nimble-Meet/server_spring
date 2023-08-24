package com.nimble.server_spring.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class LocalLoginRequestDto {

  @NotNull
  @Email
  @Schema(example = "user@email.com", description = "사용자 이메일")
  private String email;

  @NotNull
  @Schema(example = "password", description = "사용자 비밀번호")
  private String password;
}
