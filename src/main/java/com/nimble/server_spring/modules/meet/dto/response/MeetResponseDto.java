package com.nimble.server_spring.modules.meet.dto.response;

import com.nimble.server_spring.modules.meet.Meet;
import com.nimble.server_spring.modules.user.dto.response.SimpleUserResponseDto;
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

  private Long id;
  private String meetName;
  private String description;
  private LocalDateTime createdAt;
  private SimpleUserResponseDto host;
  private List<MemberResponseDto> members;

  public static MeetResponseDto fromMeet(Meet meet) {
    return MeetResponseDto.builder()
        .id(meet.getId())
        .meetName(meet.getMeetName())
        .description(meet.getDescription())
        .createdAt(meet.getCreatedAt())
        .host(SimpleUserResponseDto.fromUser(meet.getHost()))
        .members(meet.getMeetMembers().stream()
            .map(MemberResponseDto::fromMeetMember)
            .toList()
        )
        .build();
  }
}
