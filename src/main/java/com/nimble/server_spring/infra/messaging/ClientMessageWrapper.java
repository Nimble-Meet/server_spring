package com.nimble.server_spring.infra.messaging;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;

public class ClientMessageWrapper {

    private final StompHeaderAccessor clientHeaderAccessor;

    private ClientMessageWrapper(StompHeaderAccessor clientHeaderAccessor) {
        this.clientHeaderAccessor = clientHeaderAccessor;
    }

    public static ClientMessageWrapper create(Message<byte[]> clientMessage) {
        StompHeaderAccessor clientHeaderAccesser = MessageHeaderAccessor.getAccessor(
            clientMessage,
            StompHeaderAccessor.class
        );
        return new ClientMessageWrapper(clientHeaderAccesser);
    }

    public String getReceiptId() {
        return clientHeaderAccessor.getReceiptId();
    }
}
