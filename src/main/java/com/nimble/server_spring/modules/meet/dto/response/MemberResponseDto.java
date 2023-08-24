package com.nimble.server_spring.modules.meet.dto.response;

import com.nimble.server_spring.modules.meet.MeetMember;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {

  @Schema(example = "1", description = "미팅 멤버 ID")
  private Long id;

  @Schema(example = "member@email.com", description = "미팅 멤버의 이메일")
  private String email;

  @Schema(example = "MemberNickName", description = "미팅 멤버의 닉네임")
  private String nickname;

  public static MemberResponseDto fromMeetMember(MeetMember meetMember) {
    return MemberResponseDto.builder()
        .id(meetMember.getId())
        .email(meetMember.getUser().getEmail())
        .nickname(meetMember.getUser().getNickname())
        .build();
  }
}
