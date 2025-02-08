package com.dev.auth.webSocket;

import com.dev.auth.webSocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

@Component
public class GroupChatMessageService {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;

    public GroupChatMessageService(WebSocketSessionManager sessionManager, ObjectMapper objectMapper) {
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
    }

    public void sendMessageToRoom(String roomId, ChatMessage chatMessage) throws IOException {
        Map<String, WebSocketSession> chatRoomSessions = sessionManager.getChatRoomSessions(roomId);
        if (chatRoomSessions != null) {
            for (Map.Entry<String, WebSocketSession> userSession : chatRoomSessions.entrySet()) {
                WebSocketSession socketSession = userSession.getValue();
                if (socketSession != null && socketSession.isOpen() &&
                        !userSession.getKey().equals(chatMessage.getSender()) ) {
                    socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                }
            }
        }
    }

}
