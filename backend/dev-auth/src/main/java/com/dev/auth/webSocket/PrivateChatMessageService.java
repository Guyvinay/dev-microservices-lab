package com.dev.auth.webSocket;

import com.dev.auth.webSocket.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.dev.auth.utility.StringLiterals.PRIVATE;

@Component
public class PrivateChatMessageService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;

    public PrivateChatMessageService(WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.objectMapper = objectMapper;
    }

    public void sendPrivateMessage(ChatMessage chatMessage) throws IOException {
        WebSocketSession receiverSession = webSocketSessionManager.getUserSession(PRIVATE , chatMessage.getReceiver());

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

}
