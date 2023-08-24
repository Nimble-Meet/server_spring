package com.nimble.server_spring.modules.meet;

import com.nimble.server_spring.modules.auth.AuthService;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequestDto;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponseDto;
import com.nimble.server_spring.modules.meet.dto.response.MemberResponseDto;
import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meet")
@RequiredArgsConstructor
@Tag(name = "Meet", description = "미팅 관련 API")
public class MeetController {

  private final AuthService authService;
  private final MeetService meetService;

  @GetMapping
  @Operation(summary = "내가 생성한 미팅과 초대 받은 미팅을 조회합니다.")
  public ResponseEntity<List<MeetResponseDto>> getMeets() {
    User currentUser = authService.getCurrentUser();
    List<Meet> meetList = meetService.getHostedOrInvitedMeets(currentUser);
    List<MeetResponseDto> meetResponseDtoList = meetList.stream().map(MeetResponseDto::fromMeet)
        .toList();
    return new ResponseEntity<>(meetResponseDtoList, HttpStatus.OK);
  }

  @PostMapping
  @Operation(summary = "미팅을 생성합니다.")
  public ResponseEntity<MeetResponseDto> createMeet(
      @RequestBody @Parameter(description = "미팅 생성 정보", required = true)
      MeetCreateRequestDto meetCreateRequestDto
  ) {
    User currentUser = authService.getCurrentUser();
    Meet meet = meetService.createMeet(
        currentUser,
        meetCreateRequestDto
    );
    MeetResponseDto meetResponseDto = MeetResponseDto.fromMeet(meet);
    return new ResponseEntity<>(meetResponseDto, HttpStatus.CREATED);
  }

  @GetMapping("/{meetId}")
  @Operation(summary = "특정 미팅을 조회합니다.")
  public ResponseEntity<MeetResponseDto> getMeet(
      @PathVariable @Parameter(description = "조회할 미팅의 ID", required = true)
      Long meetId
  ) {
    User currentUser = authService.getCurrentUser();
    Meet meet = meetService.getMeet(
        currentUser,
        meetId
    );

    MeetResponseDto meetResponseDto = MeetResponseDto.fromMeet(meet);
    return new ResponseEntity<>(meetResponseDto, HttpStatus.OK);
  }

  @PostMapping("/{meetId}/member")
  @Operation(summary = "특정 미팅에 멤버를 초대합니다.")
  public ResponseEntity<MemberResponseDto> invite(
      @PathVariable @Parameter(description = "멤버를 초대할 미팅의 ID", required = true)
      Long meetId,
      @RequestBody @Parameter(description = "초대할 멤버의 정보", required = true)
      MeetInviteRequestDto meetInviteRequestDto
  ) {
    User currentUser = authService.getCurrentUser();
    MeetMember meetMember = meetService.invite(
        currentUser,
        meetId,
        meetInviteRequestDto
    );

    MemberResponseDto memberResponseDto = MemberResponseDto.fromMeetMember(meetMember);
    return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
  }

  @DeleteMapping("/{meetId}/member/{memberId}")
  @Operation(summary = "특정 미팅에서 멤버를 강퇴합니다.")
  public ResponseEntity<MemberResponseDto> kickOut(
      @PathVariable @Parameter(description = "멤버를 강퇴할 미팅의 ID", required = true)
      Long meetId,
      @PathVariable @Parameter(description = "강퇴할 멤버의 ID", required = true)
      Long memberId
  ) {
    User currentUser = authService.getCurrentUser();
    MeetMember meetMember = meetService.kickOut(
        currentUser,
        meetId,
        memberId
    );

    MemberResponseDto memberResponseDto = MemberResponseDto.fromMeetMember(meetMember);
    return new ResponseEntity<>(memberResponseDto, HttpStatus.OK);
  }
}
