package com.nimble.server_spring.modules.meet.dto.response;

import com.nimble.server_spring.modules.meet.MeetUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetUserResponseDto {

    @Schema(example = "1", description = "미팅 멤버 ID")
    private Long id;

    @Schema(example = "member@email.com", description = "미팅 멤버의 이메일")
    private String email;

    @Schema(example = "MemberNickName", description = "미팅 멤버의 닉네임")
    private String nickname;

    @Schema(example = "MEMBER", description = "미팅 멤버의 권한")
    private String role;

    public static MeetUserResponseDto fromMeetUser(MeetUser meetUser) {
        return MeetUserResponseDto.builder()
            .id(meetUser.getId())
            .email(meetUser.getUser().getEmail())
            .nickname(meetUser.getUser().getNickname())
            .role(meetUser.getMeetUserRole().name())
            .build();
    }
}
