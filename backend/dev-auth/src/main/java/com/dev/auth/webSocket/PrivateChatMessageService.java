package com.dev.auth.webSocket;

import com.dev.auth.webSocket.dto.ChatMessage;
import com.dev.auth.webSocket.dto.STATUS;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.dev.auth.utility.StringLiterals.PRIVATE;

/**
 * Service for handling private chat messages in a WebSocket-based chat system.
 */
@Component
public class PrivateChatMessageService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;

    /**
     * Constructor for PrivateChatMessageService.
     *
     * @param webSocketSessionManager Manages WebSocket user sessions.
     * @param objectMapper            Converts objects to JSON format.
     */
    public PrivateChatMessageService(WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.objectMapper = objectMapper;
    }

    /**
     * Sends a private chat message to the receiver.
     *
     * @param chatMessage The chat message to be sent.
     * @throws IOException If an error occurs while sending the message.
     */
    public void sendPrivateMessage(ChatMessage chatMessage) throws IOException {

        // Retrieve the WebSocket session of the receiver in private chat
        WebSocketSession receiverSession = webSocketSessionManager.getUserSession(
                PRIVATE, chatMessage.getReceiver());

        // Send the message if the session exists and is open
        if (receiverSession != null && receiverSession.isOpen()) {
            chatMessage.setStatus(STATUS.DELIVERED);
            receiverSession.sendMessage(
                    new TextMessage(
                            objectMapper.writeValueAsString(chatMessage)
                    )
            );
        } else {
            /**
             * Fallback handling if receiverSession is null means receiver not connected
             * send to sender session about receiver not connected.
             */
            WebSocketSession senderSession = webSocketSessionManager.getUserSession(
                    PRIVATE,
                    chatMessage.getSender()
            );

            if (senderSession != null && senderSession.isOpen()) {
                chatMessage.setMessage("Receiver: "+ chatMessage.getReceiver()+", not connected.");
                chatMessage.setStatus(STATUS.USER_NOT_CONNECTED);
                senderSession.sendMessage(
                        new TextMessage(
                                objectMapper.writeValueAsString(chatMessage)
                        )
                );
            }
        }
    }
}
