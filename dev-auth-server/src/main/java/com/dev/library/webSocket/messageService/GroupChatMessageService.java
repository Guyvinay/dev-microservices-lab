package com.dev.library.webSocket.messageService;

import com.dev.library.elastic.service.MessageElasticSyncService;
import com.dev.library.webSocket.WebSocketSessionManager;
import com.dev.library.webSocket.dto.ChatMessagePayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

/**
 * Service for handling group chat messages in a WebSocket-based chat system.
 */
@Component
public class GroupChatMessageService {

    private final WebSocketSessionManager sessionManager;
    private final ObjectMapper objectMapper;
    private final MessageElasticSyncService messageElasticSyncService;


    /**
     * Constructor for GroupChatMessageService.
     *
     * @param sessionManager Manages WebSocket user sessions.
     * @param objectMapper   Converts objects to JSON format.
     */
    public GroupChatMessageService(WebSocketSessionManager sessionManager, ObjectMapper objectMapper, MessageElasticSyncService messageElasticSyncService) {
        this.sessionManager = sessionManager;
        this.objectMapper = objectMapper;
        this.messageElasticSyncService = messageElasticSyncService;
    }

    /**
     * Sends a chat message to all users in the specified group chat room, except the sender.
     *
     * @param roomId      The unique identifier of the chat room.
     * @param chatMessage The chat message to be sent.
     * @throws IOException If an error occurs while sending the message.
     */
    public void sendMessageToRoom(String roomId, ChatMessagePayload chatMessage) throws IOException {

        // Retrieve all active WebSocket sessions in the chat room
        Map<String, WebSocketSession> chatRoomSessions = sessionManager.getChatRoomSessions(roomId);
        if (chatRoomSessions != null) {
            for (Map.Entry<String, WebSocketSession> userSession : chatRoomSessions.entrySet()) {
                WebSocketSession socketSession = userSession.getValue();

                // Ensure session is open and exclude the sender from receiving their own message
                if (socketSession != null && socketSession.isOpen() &&
                        !userSession.getKey().equals(chatMessage.getSender()) ) {
                    socketSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                }
            }
        }
    }
}
