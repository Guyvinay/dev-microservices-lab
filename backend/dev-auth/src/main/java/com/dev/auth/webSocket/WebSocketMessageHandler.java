package com.dev.auth.webSocket;

import com.dev.auth.webSocket.dto.ChatMessage;
import com.dev.auth.webSocket.dto.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class WebSocketMessageHandler extends TextWebSocketHandler {


    private final WebSocketSessionManager webSocketSessionManager;
    private final PrivateChatMessageService privateChatMessageService;
    private final ObjectMapper objectMapper;
    private final GroupChatService  groupChatService;

    public WebSocketMessageHandler(WebSocketSessionManager webSocketSessionManager, PrivateChatMessageService privateChatMessageService, ObjectMapper objectMapper, GroupChatService groupChatService) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.privateChatMessageService = privateChatMessageService;
        this.objectMapper = objectMapper;
        this.groupChatService = groupChatService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Retrieve username from session attributes
        String username = (String) session.getAttributes().get("username");

        if (username != null) {
            webSocketSessionManager.addSession(username, session);
            System.out.println("WebSocket connection established for user: " + username);
        } else {
            System.out.println("Unauthorized WebSocket connection attempt");
            session.close();
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sender = (String) session.getAttributes().get("username");

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        if (chatMessage != null && chatMessage.getSender() == null) {
            chatMessage.setSender(sender);
        }

        if (chatMessage != null && chatMessage.getType() == MessageType.PRIVATE) {
            privateChatMessageService.sendPrivateMessage(chatMessage);
        }
        System.out.println("message receive from : " + sender + ", " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Retrieve username from session attributes
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            webSocketSessionManager.removeSession(username);
            System.out.println("User disconnected: " + username);
        }
    }
}
