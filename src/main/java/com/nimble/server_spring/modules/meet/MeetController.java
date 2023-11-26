package com.nimble.server_spring.modules.meet;

import static com.nimble.server_spring.infra.apidoc.SwaggerConfig.JWT_ACCESS_TOKEN;

import com.nimble.server_spring.infra.apidoc.ApiErrorCodes;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.auth.AuthService;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequestDto;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponseDto;
import com.nimble.server_spring.modules.meet.dto.response.MemberResponseDto;
import com.nimble.server_spring.modules.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meet")
@RequiredArgsConstructor
@Tag(name = "Meet", description = "미팅 관련 API")
@SecurityRequirement(name = JWT_ACCESS_TOKEN)
public class MeetController {

    private final AuthService authService;
    private final MeetService meetService;
    private final MeetRepository meetRepository;

    @GetMapping
    @Operation(summary = "미팅 목록 조회", description = "내가 생성했거나 초대 받은 미팅을 조회합니다.")
    public ResponseEntity<List<MeetResponseDto>> getMeets() {
        User currentUser = authService.getCurrentUser();
        List<Meet> meetList = meetRepository.findHostedOrInvitedMeetsByUserId(currentUser.getId());
        List<MeetResponseDto> meetResponseDtoList = meetList.stream()
            .map(MeetResponseDto::fromMeet)
            .toList();
        return new ResponseEntity<>(meetResponseDtoList, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "미팅 생성", description = "미팅 생성 정보를 이용해서 미팅을 생성합니다.")
    public ResponseEntity<MeetResponseDto> createMeet(
        @RequestBody @Validated @Parameter(description = "미팅 생성 정보", required = true)
        MeetCreateRequestDto meetCreateRequestDto
    ) {
        User currentUser = authService.getCurrentUser();
        Meet meet = meetService.createMeet(
            currentUser,
            meetCreateRequestDto
        );
        return new ResponseEntity<>(
            MeetResponseDto.fromMeet(meet),
            HttpStatus.CREATED
        );
    }

    @GetMapping("/{meetId}")
    @Operation(summary = "미팅 조회", description = "특정 미팅을 조회합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND
    })
    public ResponseEntity<MeetResponseDto> getMeet(
        @PathVariable @Parameter(description = "조회할 미팅의 ID", required = true)
        Long meetId
    ) {
        User currentUser = authService.getCurrentUser();
        Meet meet = meetRepository.findMeetByIdIfHostedOrInvited(meetId, currentUser.getId())
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        return new ResponseEntity<>(
            MeetResponseDto.fromMeet(meet),
            HttpStatus.OK
        );
    }

    @PostMapping("/{meetId}/member")
    @Operation(summary = "멤버 초대", description = "email에 해당하는 사용자를 특정 미팅에 초대합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND
    })
    public ResponseEntity<MemberResponseDto> invite(
        @PathVariable @Parameter(description = "멤버를 초대할 미팅의 ID", required = true)
        Long meetId,
        @RequestBody @Validated @Parameter(description = "초대할 멤버의 정보", required = true)
        MeetInviteRequestDto meetInviteRequestDto
    ) {
        User currentUser = authService.getCurrentUser();
        MeetMember meetMember = meetService.invite(
            currentUser,
            meetId,
            meetInviteRequestDto
        );

        return new ResponseEntity<>(
            MemberResponseDto.fromMeetMember(meetMember),
            HttpStatus.OK
        );
    }

    @DeleteMapping("/{meetId}/member/{memberId}")
    @Operation(summary = "멤버 강퇴", description = "특정 미팅에서 멤버를 강퇴합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND
    })
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

        return new ResponseEntity<>(
            MemberResponseDto.fromMeetMember(meetMember),
            HttpStatus.OK
        );
    }
}
