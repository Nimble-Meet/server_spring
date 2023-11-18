package com.nimble.server_spring.modules.chat;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.error.BadRequestReason;
import com.nimble.server_spring.infra.error.ErrorResponse;
import com.nimble.server_spring.infra.error.BindingResultWrapper;
import com.nimble.server_spring.modules.chat.dto.request.ChatTalkRequestDto;
import com.nimble.server_spring.modules.chat.dto.request.ChatEnterRequestDto;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto;
import com.nimble.server_spring.modules.meet.MeetMember;
import com.nimble.server_spring.modules.meet.MeetMemberRepository;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessageSendingOperations template;
    private final ChatRepository chatRepository;
    private final MeetMemberRepository meetMemberRepository;
    private final ObjectMapper objectMapper;

    @MessageMapping("/chat/enter")
    public void enterUser(
        @Payload @Validated ChatEnterRequestDto chatEnterRequestDto,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        log.info("유저 입장: {}", chatEnterRequestDto);
        MeetMember meetMember = meetMemberRepository
            .findById(chatEnterRequestDto.getMemberId())
            .orElseThrow(
                () -> new ErrorCodeException(ErrorCode.MEET_MEMBER_NOT_FOUND)
            );
        meetMember.enterMeet();
        meetMemberRepository.save(meetMember);

        Chat chat = chatEnterRequestDto.toChatEntity();
        chatRepository.save(chat);

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        sessionAttributes.put("memberId", chat.getMemberId());
        sessionAttributes.put("email", chat.getEmail());

        ChatResponseDto chatResponseDto = ChatResponseDto.fromChat(chat);
        template.convertAndSend("/subscribe/chat/meet/" + chat.getMeetId(), chatResponseDto);
    }

    @MessageMapping("/chat/talk")
    public void sendMessage(@Payload @Validated ChatTalkRequestDto chatTalkRequestDto) {
        log.info("채팅: {}", chatTalkRequestDto);
        Chat chat = chatTalkRequestDto.toChatEntity();
        chatRepository.save(chat);

        Long meetId = chatTalkRequestDto.getMeetId();
        ChatResponseDto chatResponseDto = ChatResponseDto.fromChat(chat);
        template.convertAndSend("/subscribe/chat/meet/" + meetId, chatResponseDto);
    }

    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        Object emailAttribute = sessionAttributes.get("email");
        Object memberIdAttribute = sessionAttributes.get("memberId");
        if (emailAttribute == null || memberIdAttribute == null) {
            return;
        }

        String email = emailAttribute.toString();
        long memberId = Long.parseLong(memberIdAttribute.toString());

        MeetMember meetMember = meetMemberRepository
            .findById(memberId)
            .orElseThrow(
                () -> new ErrorCodeException(ErrorCode.MEET_MEMBER_NOT_FOUND)
            );
        meetMember.leaveMeet();
        meetMemberRepository.save(meetMember);

        Chat chat = Chat.builder()
            .chatType(ChatType.LEAVE)
            .email(email)
            .memberId(memberId)
            .build();
        chatRepository.save(chat);

        Long meetId = meetMember.getMeet().getId();
        ChatResponseDto chatResponseDto = ChatResponseDto.fromChat(chat);
        template.convertAndSend("/subscribe/chat/meet/" + meetId, chatResponseDto);
        log.info("유저 퇴장: {}", chatResponseDto);
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
            BadRequestReason badRequestReason = BadRequestReason.create(
                fieldName,
                invalidFormatException.getTargetType(),
                invalidFormatException.getValue(),
                objectMapper
            );
            return badRequestReason.toErrorResponse();
        } else if (exception instanceof MethodArgumentNotValidException) {
            log.info("MethodArgumentNotValidException occurred");
            BindingResult bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
            return BindingResultWrapper.create(bindingResult, objectMapper).toErrorResponse();
        }
        return ErrorCode.INTERNAL_SERVER_ERROR.toErrorResponse();
    }
}
