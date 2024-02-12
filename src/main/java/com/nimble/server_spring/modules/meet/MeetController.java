package com.nimble.server_spring.modules.meet;

import static com.nimble.server_spring.infra.apidoc.SwaggerConfig.JWT_ACCESS_TOKEN;

import com.nimble.server_spring.infra.apidoc.ApiErrorCodes;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.modules.chat.ChatRepository;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequestDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequestDto;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponseDto;
import com.nimble.server_spring.modules.meet.dto.response.MeetUserResponseDto;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

    private final UserService userService;
    private final MeetService meetService;
    private final MeetRepository meetRepository;
    private final MeetUserRepository meetUserRepository;
    private final ChatRepository chatRepository;

    @GetMapping
    @Operation(summary = "미팅 목록 조회", description = "내가 생성했거나 초대 받은 미팅을 조회합니다.")
    public ResponseEntity<List<MeetResponseDto>> getMeets(Principal principal) {
        User currentUser = userService.getUserByPrincipalLazy(principal);

        List<Meet> meetList = meetRepository.findParticipatedMeets(currentUser.getId());
        List<MeetResponseDto> meetResponseDtoList = meetList.stream()
            .map(MeetResponseDto::fromMeet)
            .toList();
        return new ResponseEntity<>(meetResponseDtoList, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "미팅 생성", description = "미팅 생성 정보를 이용해서 미팅을 생성합니다.")
    public ResponseEntity<MeetResponseDto> createMeet(
        @RequestBody @Validated @Parameter(description = "미팅 생성 정보", required = true)
        MeetCreateRequestDto meetCreateRequestDto,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipal(principal);

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
        ErrorCode.MEET_NOT_FOUND,
        ErrorCode.NOT_MEET_USER_FORBIDDEN
    })
    public ResponseEntity<MeetResponseDto> getMeet(
        @PathVariable @Parameter(description = "조회할 미팅의 ID", required = true)
        Long meetId,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipalLazy(principal);

        Meet meet = meetRepository.findMeetById(meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_NOT_FOUND));

        if (!meet.isParticipatedBy(currentUser)) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN);
        }

        return new ResponseEntity<>(
            MeetResponseDto.fromMeet(meet),
            HttpStatus.OK
        );
    }

    @PostMapping("/{meetId}/member")
    @Operation(summary = "멤버 초대", description = "email에 해당하는 사용자를 특정 미팅에 초대합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND,
        ErrorCode.NOT_MEET_HOST_FORBIDDEN,
        ErrorCode.MEET_INVITE_LIMIT_OVER,
        ErrorCode.USER_NOT_FOUND_BY_EMAIL,
        ErrorCode.USER_ALREADY_INVITED
    })
    public ResponseEntity<MeetUserResponseDto> invite(
        @PathVariable @Parameter(description = "멤버를 초대할 미팅의 ID", required = true)
        Long meetId,
        @RequestBody @Validated @Parameter(description = "초대할 멤버의 정보", required = true)
        MeetInviteRequestDto meetInviteRequestDto,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipalLazy(principal);

        MeetUser meetUser = meetService.invite(
            currentUser,
            meetId,
            meetInviteRequestDto
        );

        return new ResponseEntity<>(
            MeetUserResponseDto.fromMeetUser(meetUser),
            HttpStatus.OK
        );
    }

    @DeleteMapping("/{meetId}/member/{meetUserId}")
    @Operation(summary = "멤버 강퇴", description = "특정 미팅에서 멤버를 강퇴합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND,
        ErrorCode.NOT_MEET_HOST_FORBIDDEN,
        ErrorCode.MEET_USER_NOT_FOUND
    })
    public ResponseEntity<MeetUserResponseDto> kickOut(
        @PathVariable @Parameter(description = "멤버를 강퇴할 미팅의 ID", required = true)
        Long meetId,
        @PathVariable @Parameter(description = "강퇴할 멤버의 ID", required = true)
        Long meetUserId,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipalLazy(principal);

        MeetUser meetUser = meetService.kickOut(
            currentUser,
            meetId,
            meetUserId
        );

        return new ResponseEntity<>(
            MeetUserResponseDto.fromMeetUser(meetUser),
            HttpStatus.OK
        );
    }

    @GetMapping("/{meetId}/chat")
    @Operation(summary = "채팅 목록 조회", description = "특정 미팅의 채팅 목록을 조회합니다.")
    @ApiErrorCodes({
        ErrorCode.NOT_MEET_USER_FORBIDDEN
    })
    public ResponseEntity<Slice<ChatResponseDto>> getChats(
        @PathVariable @Parameter(description = "채팅 목록을 조회할 미팅의 ID", required = true)
        Long meetId,
        @RequestParam @Parameter(description = "현재 페이지")
        Integer page,
        @RequestParam @Parameter(description = "페이지 크기")
        Integer size,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipalLazy(principal);
        if (!meetUserRepository.existsByUser_IdAndMeet_Id(
            currentUser.getId(),
            meetId
        )) {
            throw new ErrorCodeException(ErrorCode.NOT_MEET_USER_FORBIDDEN);
        }

        Slice<ChatResponseDto> chatResponsDtoSlice = chatRepository.findAllByMeetId(
            meetId,
            PageRequest.of(page, size, Sort.by(Direction.DESC, "createdAt"))
        );

        return new ResponseEntity<>(chatResponsDtoSlice, HttpStatus.OK);
    }
}
