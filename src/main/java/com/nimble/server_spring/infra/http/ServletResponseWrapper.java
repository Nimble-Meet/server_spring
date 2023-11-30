package com.nimble.server_spring.infra.http;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletResponseWrapper {

    private final HttpServletResponse response;

    private ServletResponseWrapper(HttpServletResponse response) {
        this.response = response;
    }

    public static ServletResponseWrapper of(HttpServletResponse response) {
        return new ServletResponseWrapper(response);
    }

    public void sendJsonResponse(
        int statusCode,
        String responseBody
    ) throws IOException {
        response.addHeader("Content-Type", "application/json; charset=UTF-8");
        response.setStatus(statusCode);
        response.getWriter().write(responseBody);
    }

}
