package com.nimble.server_spring.modules.meet;

import static com.nimble.server_spring.infra.apidoc.SwaggerConfig.JWT_ACCESS_TOKEN;

import com.nimble.server_spring.infra.apidoc.ApiErrorCodes;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.modules.chat.ChatService;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto;
import com.nimble.server_spring.modules.meet.dto.request.MeetCreateRequest;
import com.nimble.server_spring.modules.meet.dto.request.MeetInviteRequest;
import com.nimble.server_spring.modules.meet.dto.response.MeetResponse;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
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
    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "미팅 생성", description = "미팅 생성 정보를 이용해서 미팅을 생성합니다.")
    public ResponseEntity<MeetResponse> createMeet(
        @RequestBody @Validated @Parameter(description = "미팅 생성 정보", required = true)
        MeetCreateRequest meetCreateRequest,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipal(principal);

        MeetResponse meetResponse = meetService.createMeet(
            currentUser,
            meetCreateRequest.toServiceRequest()
        );
        return new ResponseEntity<>(meetResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "미팅 목록 조회", description = "내가 생성했거나 초대 받은 미팅을 조회합니다.")
    public ResponseEntity<List<MeetResponse>> getMeets(Principal principal) {
        User currentUser = userService.getUserByPrincipalLazy(principal);

        List<MeetResponse> meetResponseList = meetService.getMeetList(currentUser);
        return new ResponseEntity<>(meetResponseList, HttpStatus.OK);
    }

    @GetMapping("/{meetId}")
    @Operation(summary = "미팅 조회", description = "특정 미팅을 조회합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND,
        ErrorCode.NOT_MEET_USER_FORBIDDEN
    })
    public ResponseEntity<MeetResponse> getMeet(
        @PathVariable @Parameter(description = "조회할 미팅의 ID", required = true)
        Long meetId,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipalLazy(principal);

        MeetResponse meetResponse = meetService.getMeet(meetId, currentUser);
        return new ResponseEntity<>(meetResponse, HttpStatus.OK);
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
    public ResponseEntity<MeetResponse> invite(
        @PathVariable @Parameter(description = "멤버를 초대할 미팅의 ID", required = true)
        Long meetId,
        @RequestBody @Validated @Parameter(description = "초대할 멤버의 정보", required = true)
        MeetInviteRequest meetInviteRequest,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipal(principal);

        MeetResponse meetResponse = meetService.invite(
            currentUser,
            meetId,
            meetInviteRequest.toServiceRequest()
        );
        return new ResponseEntity<>(meetResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{meetId}/member/{meetUserId}")
    @Operation(summary = "멤버 강퇴", description = "특정 미팅에서 멤버를 강퇴합니다.")
    @ApiErrorCodes({
        ErrorCode.MEET_NOT_FOUND,
        ErrorCode.NOT_MEET_HOST_FORBIDDEN,
        ErrorCode.MEET_USER_NOT_FOUND
    })
    public ResponseEntity<MeetResponse> kickOut(
        @PathVariable @Parameter(description = "멤버를 강퇴할 미팅의 ID", required = true)
        Long meetId,
        @PathVariable @Parameter(description = "강퇴할 멤버의 ID", required = true)
        Long meetUserId,
        Principal principal
    ) {
        User currentUser = userService.getUserByPrincipal(principal);

        MeetResponse meetResponse = meetService.kickOut(
            currentUser,
            meetId,
            meetUserId
        );
        return new ResponseEntity<>(meetResponse, HttpStatus.OK);
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

        Slice<ChatResponseDto> chatResponsDtoSlice =
            chatService.getChatList(currentUser, meetId, size, page);
        return new ResponseEntity<>(chatResponsDtoSlice, HttpStatus.OK);
    }
}
