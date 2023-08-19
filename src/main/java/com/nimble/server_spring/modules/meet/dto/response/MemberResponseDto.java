package com.nimble.server_spring.modules.meet.dto.response;

import com.nimble.server_spring.modules.meet.MeetMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String email;
    private String nickname;

    public static MemberResponseDto fromMeetMember(MeetMember meetMember) {
        return MemberResponseDto.builder()
                .id(meetMember.getId())
                .email(meetMember.getUser().getEmail())
                .nickname(meetMember.getUser().getNickname())
                .build();
    }
}
