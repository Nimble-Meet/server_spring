package com.nimble.server_spring.modules.meet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class MeetCreateRequestDto {

  @NotBlank
  @Length(min = 2, max = 24)
  @Schema(example = "My Meet", description = "미팅 이름")
  private String meetName;

  @Length(min = 0, max = 48)
  @Schema(example = "예시 미팅 입니다.", description = "미팅 설명")
  private String description;
}
