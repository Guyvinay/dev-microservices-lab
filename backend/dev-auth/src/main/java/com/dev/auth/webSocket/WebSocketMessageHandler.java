package com.dev.auth.webSocket;

import com.dev.auth.webSocket.dto.ChatMessage;
import com.dev.auth.webSocket.dto.MessageType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;

public class WebSocketMessageHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final PrivateChatMessageService privateChatMessageService;
    private final ObjectMapper objectMapper;
    private final GroupChatMessageService groupChatMessageService;

    public WebSocketMessageHandler(WebSocketSessionManager webSocketSessionManager, PrivateChatMessageService privateChatMessageService, ObjectMapper objectMapper, GroupChatMessageService groupChatMessageService) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.privateChatMessageService = privateChatMessageService;
        this.objectMapper = objectMapper;
        this.groupChatMessageService = groupChatMessageService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        URI uri = session.getUri();
        if (uri == null) {
            session.close();
            return;
        }

        String uriPath = uri.getPath();
        String username = (String) session.getAttributes().get("username");

        if (username == null) {
            System.out.println("Unauthorized WebSocket connection attempt");
            session.close();
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }

        if (uriPath.startsWith("/dev-auth/ws/chat/private")) {
            webSocketSessionManager.addUserToPrivateChat(username, session);
            System.out.println("WebSocket connection established for user: " + username);
        } else if (uriPath.startsWith("/dev-auth/ws/chat/group")) {
            String roomId = extractRoomId(uriPath);
            System.out.println("WebSocket connection established for user: " + username);
            webSocketSessionManager.addUserSession(roomId, username, session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sender = (String) session.getAttributes().get("username");
        String uriPath = session.getUri().getPath();

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        if (chatMessage != null && chatMessage.getSender() == null) {
            chatMessage.setSender(sender);
        }
        if(chatMessage != null) {
            if (uriPath.startsWith("/dev-auth/ws/chat/private")) {
                privateChatMessageService.sendPrivateMessage(chatMessage);
            }
            else if(uriPath.startsWith("/dev-auth/ws/chat/group")) {
                String roomId = extractRoomId(uriPath);
                groupChatMessageService.sendMessageToRoom(roomId, chatMessage);
            }
        }
        System.out.println("message receive from : " + sender + ", " + message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Retrieve username from session attributes
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            webSocketSessionManager.removeUserFromChat("", username);
            System.out.println("User disconnected: " + username);
        }
    }

    private String extractRoomId(String uriPath) {
        return uriPath.substring(uriPath.lastIndexOf("/") + 1);
    }
}
