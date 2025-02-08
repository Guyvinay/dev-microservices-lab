package com.dev.auth.webSocket;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

public class WebSocketMessageHandler extends AbstractWebSocketHandler {

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Retrieve username from session attributes
        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            System.out.println("WebSocket connection established for user: " + username);
        } else {
            System.out.println("Unauthorized WebSocket connection attempt");
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

    }

    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    }

    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
    }

    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }


}
