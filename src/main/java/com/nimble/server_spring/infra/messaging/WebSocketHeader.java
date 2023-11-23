package com.nimble.server_spring.infra.messaging;

import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public class WebSocketHeader {

    private final StompHeaderAccessor headerAccessor;

    private WebSocketHeader(StompCommand stompCommand, String receiptId, String message) {
        headerAccessor = StompHeaderAccessor.create(
            stompCommand
        );
        headerAccessor.setReceiptId(receiptId);
        headerAccessor.setMessage(message);
    }

    public static WebSocketHeader create(
        StompCommand stompCommand, String receiptId, String message
    ) {
        return new WebSocketHeader(stompCommand, receiptId, message);
    }

    public MessageHeaders toMessageHeaders() {
        return headerAccessor.getMessageHeaders();
    }
}
