package com.nimble.server_spring.modules.chat;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.messaging.WebSocketExceptionHandler;
import com.nimble.server_spring.infra.messaging.WebSocketControllerSupport;
import com.nimble.server_spring.modules.chat.dto.request.EnterChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.LeaveChatServiceRequest;
import com.nimble.server_spring.modules.chat.dto.request.TalkChatRequest;
import com.nimble.server_spring.modules.chat.dto.response.ChatResponse;
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

    public static final String SESSION_EMAIL_KEY = "session_email";
    public static final String SESSION_MEET_ID_KEY = "session_meet_id";
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

        ChatResponse chatResponse = chatService.enterChat(
            EnterChatServiceRequest.create(currentUser, meetId)
        );

        Optional.ofNullable(headerAccessor.getSessionAttributes())
            .ifPresent(
                attributes -> {
                    attributes.put(SESSION_EMAIL_KEY, chatResponse.getEmail());
                    attributes.put(SESSION_MEET_ID_KEY, chatResponse.getMeetId());
                }
            );
        template.convertAndSend("/subscribe/chat/meet/" + meetId, chatResponse);
    }

    @MessageMapping("/meet/{meetId}/chat/talk")
    public void sendMessage(
        @DestinationVariable Long meetId,
        @Payload @Validated TalkChatRequest talkChatRequest,
        SimpMessageHeaderAccessor headerAccessor
    ) {
        User currentUser = Optional.ofNullable(headerAccessor.getUser())
            .map(userService::getUserByPrincipalLazy)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));

        ChatResponse chatResponse
            = chatService.talkChat(talkChatRequest.toServiceRequest(currentUser, meetId));

        template.convertAndSend("/subscribe/chat/meet/" + meetId, chatResponse);
    }

    @EventListener
    public void webSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        LeaveChatServiceRequest leaveChatServiceRequest = Optional
            .ofNullable(headerAccessor.getSessionAttributes())
            .map(LeaveChatServiceRequest::fromAttributesMap)
            .orElse(null);
        boolean isNotAuthenticated = Objects.isNull(leaveChatServiceRequest) ||
                                     Objects.isNull(leaveChatServiceRequest.getEmail()) ||
                                     Objects.isNull(leaveChatServiceRequest.getMeetId());
        if (isNotAuthenticated) {
            return;
        }

        ChatResponse chatResponse = chatService.leaveChat(leaveChatServiceRequest);

        template.convertAndSend(
            "/subscribe/chat/meet/" + chatResponse.getMeetId(),
            chatResponse
        );
    }
}
