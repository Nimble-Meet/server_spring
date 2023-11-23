package com.nimble.server_spring.infra.socket;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.jwt.AuthToken;
import com.nimble.server_spring.infra.jwt.AuthTokenProvider;
import com.nimble.server_spring.infra.http.HeaderUtils;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final AuthTokenProvider authTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor headerAccessor = MessageHeaderAccessor.getAccessor(
            message,
            StompHeaderAccessor.class
        );
        assert headerAccessor != null;
        if (!Objects.equals(headerAccessor.getCommand(), StompCommand.CONNECT)) {
            return message;
        }

        Authentication authentication = authenticate(headerAccessor);
        headerAccessor.setUser(authentication);

        return message;
    }

    private Authentication authenticate(StompHeaderAccessor headerAccessor) {
        String authToken = headerAccessor.getFirstNativeHeader(
            HeaderUtils.AUTHORIZATION_HEADER
        );
        AuthToken accessToken = authTokenProvider.createAccessTokenOf(authToken);
        if (!accessToken.validate()) {
            throw new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST);
        }

        return authTokenProvider.getAuthentication(accessToken);
    }
}
