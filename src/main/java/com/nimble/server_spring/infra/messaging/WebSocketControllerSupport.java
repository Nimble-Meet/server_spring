package com.nimble.server_spring.infra.messaging;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.nimble.server_spring.infra.error.ErrorCodeException;
import com.nimble.server_spring.infra.error.ErrorResponse;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.messaging.simp.annotation.SendToUser;

public abstract class WebSocketControllerSupport {

    protected WebSocketExceptionHandler webSocketExceptionHandler;

    public WebSocketControllerSupport(WebSocketExceptionHandler webSocketExceptionHandler) {
        this.webSocketExceptionHandler = webSocketExceptionHandler;
    }

    @MessageExceptionHandler(ErrorCodeException.class)
    @SendToUser(value = "/queue/error", broadcast = false)
    public ErrorResponse handleErrorCodeException(ErrorCodeException ex) {
        return webSocketExceptionHandler.handleErrorCodeException(ex);
    }

    @MessageExceptionHandler(MethodArgumentNotValidException.class)
    @SendToUser(value = "/queue/error", broadcast = false)
    public ErrorResponse handleMethodArgumentNotValidException(
        MethodArgumentNotValidException ex
    ) {
        return webSocketExceptionHandler.handleMethodArgumentNotValidException(ex);
    }

    @MessageExceptionHandler
    @SendToUser(value = "/queue/error", broadcast = false)
    public ErrorResponse handleUnknownException(Throwable ex) {
        if (ex instanceof MessageConversionException
            && ex.getCause() instanceof InvalidFormatException invalidFormatException) {
            return webSocketExceptionHandler.handleInvalidFormatException(invalidFormatException);
        }
        return webSocketExceptionHandler.handleUnknownException(ex);
    }
}
