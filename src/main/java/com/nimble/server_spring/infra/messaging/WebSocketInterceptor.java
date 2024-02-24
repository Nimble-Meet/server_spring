package com.nimble.server_spring.infra.messaging;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.http.BearerTokenParser;
import com.nimble.server_spring.infra.jwt.AuthTokenManager;
import com.nimble.server_spring.infra.jwt.JwtTokenType;
import io.jsonwebtoken.Claims;
import java.util.Collection;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketInterceptor implements ChannelInterceptor {

    private final AuthTokenManager authTokenManager;

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
            BearerTokenParser.AUTHORIZATION_HEADER
        );

        Claims tokenClaims = authTokenManager.getTokenClaims(authToken, JwtTokenType.ACCESS)
            .orElseThrow(() -> new ErrorCodeException(ErrorCode.UNAUTHENTICATED_REQUEST));
        Collection<? extends SimpleGrantedAuthority> authorities =
            authTokenManager.getAuthorities(tokenClaims);
        User principal = new User(tokenClaims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }
}
