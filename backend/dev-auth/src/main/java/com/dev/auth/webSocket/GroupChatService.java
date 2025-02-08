package com.dev.auth.webSocket;

import com.dev.auth.webSocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Component
public class GroupChatService {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    public GroupChatService(WebSocketSessionManager sessionManager, ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    public void sendMessageToRoom(String roomId, ChatMessage chatMessage) throws IOException {
        Map<String, WebSocketSession> usersSession = sessionManager.getUsersInRoom(roomId);
        if (usersSession != null) {
            for (Map.Entry<String, WebSocketSession> userSession : usersSession.entrySet()) {
                WebSocketSession socketSession = userSession.getValue();
                if (socketSession != null && socketSession.isOpen()) {
                    socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                }
            }
        }
    }

}
