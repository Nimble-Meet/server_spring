package com.nimble.server_spring.infra.messaging;

import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.error.ErrorResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketSubProtocolErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
        Message<byte[]> clientMessage,
        @NonNull Throwable ex
    ) {
        log.error("Exception thrown while handling STOMP subscribe request", ex);

        if (!(ex.getCause() instanceof ErrorCodeException)) {
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }

        try {
            ErrorCodeException errorCodeException = (ErrorCodeException) ex.getCause();

            ClientMessageWrapper clientMessageWrapper = ClientMessageWrapper.create(clientMessage);
            WebSocketHeader webSocketHeader = WebSocketHeader.create(
                StompCommand.ERROR,
                clientMessageWrapper.getReceiptId(),
                errorCodeException.getMessage()
            );

            ErrorResponse errorResponse = errorCodeException.getErrorCode().toErrorResponse();

            return MessageBuilder.createMessage(
                errorResponse.toJsonByteArray(objectMapper),
                webSocketHeader.toMessageHeaders()
            );
        } catch (Exception e) {
            log.error("Unexpected Exception thrown while handling StompSubError", e);
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }
    }
}
