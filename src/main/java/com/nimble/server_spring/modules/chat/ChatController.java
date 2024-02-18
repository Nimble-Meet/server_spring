package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.messaging.WebSocketExceptionHandler;
import com.nimble.server_spring.infra.messaging.WebSocketControllerSupport;
import com.nimble.server_spring.modules.chat.dto.request.ChatTalkRequest;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponseDto;
import com.nimble.server_spring.modules.user.User;
import com.nimble.server_spring.modules.user.UserService;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Transactional
@Controller
public class ChatController extends WebSocketControllerSupport {

    private static final String MEET_USER_ID_KEY = "meetUserId";
    private final SimpMessageSendingOperations template;
    private final UserService userService;
    private final ChatService chatService;

    @Autowired
    public ChatController(
        WebSocketExceptionHandler webSocketExceptionHandler,
        SimpMessageSendingOperations template,
        UserService userService,
        ChatService chatService
    ) {
        super(webSocketExceptionHandler);
        this.template = template;
        this.userService = userService;
        this.chatService = chatService;
    }

    @MessageMapping("/meet/{meetId}/chat/enter")
    public void enterUser(
        @DestinationVariable Long meetId,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        User currentUser = Optional.ofNullable(headerAccessor.getUser())
            .map(userService::getUserByPrincipalLazy)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));

        Chat chat = chatService.enterChat(currentUser, meetId);

        Optional.ofNullable(headerAccessor.getSessionAttributes())
            .ifPresent(
                attributes -> attributes.put(MEET_USER_ID_KEY, chat.getMeetUser().getId())
            );
        template.convertAndSend(
            "/subscribe/chat/meet/" + meetId,
            ChatResponseDto.fromChat(chat)
        );
    }

    @MessageMapping("/meet/{meetId}/chat/talk")
    public void sendMessage(
        @DestinationVariable Long meetId,
        @Payload @Validated ChatTalkRequest chatTalkRequest,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        User currentUser = Optional.ofNullable(headerAccessor.getUser())
            .map(userService::getUserByPrincipalLazy)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));

        Chat chat = chatService.talkChat(currentUser, meetId, chatTalkRequest.toServiceRequest());

        template.convertAndSend(
            "/subscribe/chat/meet/" + meetId,
            ChatResponseDto.fromChat(chat)
        );
    }

    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        Long meetUserId = Optional.ofNullable(headerAccessor.getSessionAttributes())
            .map(attributes -> attributes.get(MEET_USER_ID_KEY))
            .map(Object::toString)
            .map(Long::parseLong)
            .orElse(null);
        if (Objects.isNull(meetUserId)) {
            return;
        }

        Chat chat = chatService.leaveChat(meetUserId);

        template.convertAndSend(
            "/subscribe/chat/meet/" + chat.getMeet().getId(),
            ChatResponseDto.fromChat(chat)
        );
    }
}
