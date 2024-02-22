package com.nimble.server_spring.modules.meet.dto.response;

import com.nimble.server_spring.modules.meet.Meet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MeetResponse {

    @Schema(example = "1", description = "미팅 ID")
    private final Long id;

    @Schema(example = "MyMeet", description = "미팅 이름")
    private final String meetName;

    @Schema(example = "예시 미팅 입니다.", description = "미팅 설명")
    private final String description;

    @Schema(example = "2023-01-01T00:00:00", description = "미팅 생성 시간")
    private final LocalDateTime createdAt;

    @Schema(description = "미팅 멤버 정보의 목록")
    private final List<MeetUserResponseDto> meetUsers;

    @Builder
    private MeetResponse(
        Long id,
        String meetName,
        String description,
        LocalDateTime createdAt,
        List<MeetUserResponseDto> meetUsers
    ) {
        this.id = id;
        this.meetName = meetName;
        this.description = description;
        this.createdAt = createdAt;
        this.meetUsers = meetUsers;
    }

    public static MeetResponse fromMeet(Meet meet) {
        return MeetResponse.builder()
            .id(meet.getId())
            .meetName(meet.getMeetName())
            .description(meet.getDescription())
            .createdAt(meet.getCreatedAt())
            .meetUsers(meet.getMeetUsers().stream()
                .map(MeetUserResponseDto::fromMeetUser)
                .toList()
            )
            .build();
    }
}
