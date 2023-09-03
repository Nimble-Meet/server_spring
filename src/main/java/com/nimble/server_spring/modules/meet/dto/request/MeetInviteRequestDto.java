package com.nimble.server_spring.modules.meet.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MeetInviteRequestDto {

  @Email
  @NotNull
  @Schema(example = "user@email.com", description = "초대할 유저의 이메일")
  private String email;
}
