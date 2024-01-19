package com.nimble.server_spring.modules.chat;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.error.NotValidReason;
import com.nimble.server_spring.infra.error.TypeMismatchReason;
import com.nimble.server_spring.infra.error.ErrorResponse;
import com.nimble.server_spring.modules.chat.dto.request.ChatTalkRequestDto;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto;
import com.nimble.server_spring.modules.meet.MeetMember;
import com.nimble.server_spring.modules.meet.MeetMemberRepository;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserRepository;
import jakarta.annotation.Nullable;
import java.security.Principal;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRepository chatRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @MessageMapping("/meet/{meetId}/chat/enter")
    public void enterUser(
        @DestinationVariable Long meetId,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        User user = Optional.ofNullable(headerAccessor.getUser())
            .map(Principal::getName)
            .flatMap(userRepository::findOneByEmail)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));

        MeetMember meetMember = meetMemberRepository
            .findByUserIdAndMeetId(user.getId(), meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_MEMBER_NOT_FOUND));

        meetMember.enterMeet();

        Chat chat = Chat.builder()
            .chatType(ChatType.ENTER)
            .meet(meetMember.getMeet())
            .meetMember(meetMember)
            .build();
        chatRepository.save(chat);

        Optional.ofNullable(headerAccessor.getSessionAttributes())
            .ifPresent(attributes -> attributes.put("memberId", meetMember.getId()));
        template.convertAndSend(
            "/subscribe/chat/meet/" + meetId,
            ChatResponseDto.fromChat(chat)
        );
    }

    @MessageMapping("/meet/{meetId}/chat/talk")
    public void sendMessage(
        @DestinationVariable Long meetId,
        @Payload @Validated ChatTalkRequestDto chatTalkRequestDto,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        User user = Optional.ofNullable(headerAccessor.getUser())
            .map(Principal::getName)
            .flatMap(userRepository::findOneByEmail)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));

        MeetMember meetMember = meetMemberRepository
            .findByUserIdAndMeetId(user.getId(), meetId)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.MEET_MEMBER_NOT_FOUND));

        Chat chat = Chat.builder()
            .chatType(ChatType.TALK)
            .message(chatTalkRequestDto.getMessage())
            .meet(meetMember.getMeet())
            .meetMember(meetMember)
            .build();
        chatRepository.save(chat);

        template.convertAndSend(
            "/subscribe/chat/meet/" + meetId,
            ChatResponseDto.fromChat(chat)
        );
    }

    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String email = Optional.ofNullable(headerAccessor.getUser())
            .map(Principal::getName)
            .orElse(null);
        MeetMember meetMember = Optional.ofNullable(headerAccessor.getSessionAttributes())
            .map(attributes -> attributes.get("memberId"))
            .map(Object::toString)
            .map(Long::parseLong)
            .flatMap(meetMemberRepository::findById)
            .orElse(null);
        if (Objects.isNull(email) || Objects.isNull(meetMember)) {
            return;
        }

        meetMember.leaveMeet();

        Chat chat = Chat.builder()
            .chatType(ChatType.LEAVE)
            .meet(meetMember.getMeet())
            .meetMember(meetMember)
            .build();
        chatRepository.save(chat);

        template.convertAndSend(
            "/subscribe/chat/meet/" + meetMember.getMeet().getId(),
            ChatResponseDto.fromChat(chat)
        );
    }

    @MessageExceptionHandler
    @SneakyThrows
    @SendToUser(value = "/queue/error", broadcast = false)
    public ErrorResponse handleException(Throwable exception) {
        log.error("STOMP Web Socket의 채팅 관련 요청 처리 중 예외가 발생했습니다.", exception);
        if (exception instanceof ErrorCodeException) {
            ErrorCode errorCode = ((ErrorCodeException) exception).getErrorCode();
            return errorCode.toErrorResponse();
        } else if (exception instanceof MessageConversionException
                   && exception.getCause() instanceof InvalidFormatException) {
            InvalidFormatException invalidFormatException = (InvalidFormatException) (exception.getCause());
            String fieldName = invalidFormatException.getPath().stream()
                .findFirst()
                .map(Reference::getFieldName)
                .orElse(null);
            TypeMismatchReason typeMismatchReason = TypeMismatchReason.create(
                fieldName,
                invalidFormatException.getTargetType(),
                invalidFormatException.getValue()
            );
            return typeMismatchReason.toErrorResponse(objectMapper);
        } else if (exception instanceof MethodArgumentNotValidException) {
            log.info("MethodArgumentNotValidException occurred");
            BindingResult bindingResult = ((MethodArgumentNotValidException) exception)
                .getBindingResult();
            if (Objects.isNull(bindingResult)) {
                log.error(
                    "MethodArgumentNotValidException 이 발생했지만, bindingResult 가 null 입니다.");
                return ErrorCode.INTERNAL_SERVER_ERROR.toErrorResponse();
            }

            if (!Objects.isNull(bindingResult.getGlobalError())) {
                log.info(
                    "Chat 메시징 처리 과정에서 GlobalError가 발생했습니다. - {}",
                    bindingResult.getGlobalError()
                );
                return ErrorResponse.createBadRequestResponse("payload가 null이거나 적절한 형식이 아닙니다.");
            }

            return NotValidReason.create(bindingResult.getFieldErrors())
                .toErrorResponse(objectMapper);
        }
        return ErrorCode.INTERNAL_SERVER_ERROR.toErrorResponse();
    }
}
