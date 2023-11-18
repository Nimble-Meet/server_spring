package com.nimble.server_spring.infra.socket;

import com.nimble.server_spring.infra.error.ErrorCode;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.error.ErrorResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompSubErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public Message<byte[]> handleClientMessageProcessingError(
        Message<byte[]> clientMessage,
        Throwable ex
    ) {
        log.error("Exception thrown while handling STOMP subscribe request", ex);

        if (!(ex.getCause() instanceof ErrorCodeException)) {
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }

        try {
            ErrorCode errorCode = ((ErrorCodeException) ex.getCause()).getErrorCode();
            String receiptId = getReceiptIdFrom(clientMessage);

            ErrorResponse errorResponse = errorCode.toErrorResponse();
            StompHeaderAccessor errorResponseAccessor = StompHeaderAccessor.create(
                StompCommand.ERROR
            );
            errorResponseAccessor.setMessage(ex.getCause().getMessage());
            errorResponseAccessor.setReceiptId(receiptId);

            return MessageBuilder.createMessage(
                errorResponse.toJsonByteArray(objectMapper),
                errorResponseAccessor.getMessageHeaders()
            );
        } catch (Exception e) {
            log.error("Unexpected Exception thrown while handling StompSubError", e);
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }
    }

    private String getReceiptIdFrom(Message<byte[]> clientMessage) {
        StompHeaderAccessor clientHeaderAccessor = MessageHeaderAccessor.getAccessor(
            clientMessage,
            StompHeaderAccessor.class
        );
        assert clientHeaderAccessor != null;
        return clientHeaderAccessor.getReceipt();
    }
}
