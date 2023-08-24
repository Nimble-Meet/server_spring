package com.nimble.server_spring.modules.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class LocalSignupRequestDto extends LocalLoginRequestDto {

  @NotEmpty
  @Length(min = 1, max = 15)
  @Schema(example = "UserNickname", description = "사용자 닉네임")
  private String nickname;
}
