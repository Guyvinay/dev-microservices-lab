package com.dev.webSocket;

import com.dev.webSocket.dto.ChatMessage;
import com.dev.webSocket.dto.ChatMessagePayload;
import com.dev.webSocket.dto.MessageType;
import com.dev.webSocket.messageService.GroupChatMessageService;
import com.dev.webSocket.messageService.OfflineMessageService;
import com.dev.webSocket.messageService.PrivateChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.net.URI;
import java.time.Instant;
import java.util.Queue;

/**
 * Handles WebSocket connections and messages for private and group chats.
 * <p>
 * This handler is responsible for:
 * - Establishing WebSocket connections.
 * - Handling incoming messages and routing them to the appropriate chat service.
 * - Removing users from active sessions when they disconnect.
 * </p>
 */
@Slf4j
public class WebSocketMessageHandler extends TextWebSocketHandler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final PrivateChatMessageService privateChatMessageService;
    private final ObjectMapper objectMapper;
    private final GroupChatMessageService groupChatMessageService;
    private final OfflineMessageService offlineMessageService;

    /**
     * Constructs a WebSocketMessageHandler.
     *
     * @param webSocketSessionManager   Manages active WebSocket sessions.
     * @param privateChatMessageService Service to handle private chat messages.
     * @param objectMapper              ObjectMapper for JSON deserialization.
     * @param groupChatMessageService   Service to handle group chat messages.
     * @param offlineMessageService     Service to handle offline message delivery.
     */
    public WebSocketMessageHandler(WebSocketSessionManager webSocketSessionManager, PrivateChatMessageService privateChatMessageService, ObjectMapper objectMapper, GroupChatMessageService groupChatMessageService, OfflineMessageService offlineMessageService) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.privateChatMessageService = privateChatMessageService;
        this.objectMapper = objectMapper;
        this.groupChatMessageService = groupChatMessageService;
        this.offlineMessageService = offlineMessageService;
    }

    /**
     * Called when a new WebSocket connection is established.
     *
     * @param session The WebSocket session.
     * @throws Exception If an error occurs during connection establishment.
     */
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
            log.info("Unauthorized WebSocket connection attempt");
            session.close();
            session.close(CloseStatus.NOT_ACCEPTABLE);
        }

        // Determine if the connection is for private or group chat and add the user to the session
        if (uriPath.startsWith("/dev-auth-server/ws/chat/private")) {
            webSocketSessionManager.addUserToPrivateChat(username, session);
            log.info("WebSocket connection established for user: {}", username);
            Queue<ChatMessage> pendingMessages = offlineMessageService.getOfflineMessages(username);
//            while(!pendingMessages.isEmpty()) {
//                privateChatMessageService.sendPrivateMessage(pendingMessages.poll());
//            }
            offlineMessageService.removeOfflineMessages(username);
        } else if (uriPath.startsWith("/dev-auth-server/ws/chat/group")) {
            String roomId = extractRoomId(uriPath);
            log.info("WebSocket connection established for user: {}, roomId: {}", username, roomId);
            webSocketSessionManager.addUserSession(roomId, username, session);
        }
    }

    /**
     * Handles incoming text messages from WebSocket clients.
     *
     * @param session The WebSocket session that sent the message.
     * @param message The incoming message.
     * @throws Exception If an error occurs while processing the message.
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String sender = (String) session.getAttributes().get("username");
        String uriPath = session.getUri().getPath();

        // Deserialize the received message
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        ChatMessagePayload messagePayload = objectMapper.readValue(message.getPayload(), ChatMessagePayload.class);

        chatMessage.setTimeStamp(Instant.now().toEpochMilli());
        if (chatMessage.getSender() == null) {
            chatMessage.setSender(sender);
            messagePayload.setSender(sender);
        }

        // Route the message based on chat type (private or group)
        if (uriPath.startsWith("/dev-auth-server/ws/chat/private")) {
            chatMessage.setType(MessageType.PRIVATE);
            messagePayload.setChatType(MessageType.PRIVATE);
            privateChatMessageService.sendPrivateMessage(messagePayload);
        } else if (uriPath.startsWith("/dev-auth-server/ws/chat/group")) {
            String roomId = extractRoomId(uriPath);
            chatMessage.setType(MessageType.GROUP);
            chatMessage.setRoomId(roomId);
            groupChatMessageService.sendMessageToRoom(roomId, chatMessage);
        }
    }

    /**
     * Called when a WebSocket connection is closed.
     *
     * @param session The WebSocket session.
     * @param status  The reason for disconnection.
     * @throws Exception If an error occurs during disconnection handling.
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // Retrieve username from session attributes
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            webSocketSessionManager.removeUserFromChat("", username);
            log.info("User disconnected: " + username);
        }
    }

    /**
     * Extracts the room ID from the WebSocket URI path.
     *
     * @param uriPath The URI path of the WebSocket connection.
     * @return The extracted room ID.
     */
    private String extractRoomId(String uriPath) {
        return uriPath.substring(uriPath.lastIndexOf("/") + 1);
    }
}
