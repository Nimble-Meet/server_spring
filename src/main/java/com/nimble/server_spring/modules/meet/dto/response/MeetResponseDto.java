package com.nimble.server_spring.modules.meet.dto.response;

import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.user.dto.response.SimpleUserResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MeetResponseDto {

    @Schema(example = "1", description = "미팅 ID")
    private Long id;

    @Schema(example = "MyMeet", description = "미팅 이름")
    private String meetName;

    @Schema(example = "예시 미팅 입니다.", description = "미팅 설명")
    private String description;

    @Schema(example = "2023-01-01T00:00:00", description = "미팅 생성 시간")
    private LocalDateTime createdAt;

    @Schema(description = "미팅 생성 유저 정보")
    private SimpleUserResponseDto host;

    @Schema(description = "미팅 멤버 정보의 목록")
    private List<MeetUserResponseDto> meetUsers;

    public static MeetResponseDto fromMeet(Meet meet) {
        return MeetResponseDto.builder()
            .id(meet.getId())
            .meetName(meet.getMeetName())
            .description(meet.getDescription())
            .createdAt(meet.getCreatedAt())
            .host(SimpleUserResponseDto.fromUser(meet.getHost()))
            .meetUsers(meet.getMeetUsers().stream()
                .map(MeetUserResponseDto::fromMeetUser)
                .toList()
            )
            .build();
    }
}
