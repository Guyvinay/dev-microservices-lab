package com.dev.webSocket.messageService;

import com.dev.elastic.service.MessageElasticSyncService;
import com.dev.webSocket.WebSocketSessionManager;
import com.dev.webSocket.dto.ChatMessageDTO;
import com.dev.webSocket.dto.ChatMessagePayload;
import com.dev.webSocket.utility.MessageUtilityWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

import static com.dev.utility.StringLiterals.PRIVATE;

/**
 * Service for handling private chat messages in a WebSocket-based chat system.
 */
@Component
@Slf4j
public class PrivateChatMessageService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final OfflineMessageService offlineMessageService;
    private final MessageElasticSyncService messageElasticSyncService;
    @Value("${elastic.index.private}")
    private String index;

    /**
     * Constructor for PrivateChatMessageService.
     *
     * @param webSocketSessionManager Manages WebSocket user sessions.
     * @param objectMapper            Converts objects to JSON format.
     */
    public PrivateChatMessageService(WebSocketSessionManager webSocketSessionManager, ObjectMapper objectMapper, OfflineMessageService offlineMessageService, MessageElasticSyncService messageElasticSyncService) {
        this.webSocketSessionManager = webSocketSessionManager;
        this.objectMapper = objectMapper;
        this.offlineMessageService = offlineMessageService;
        this.messageElasticSyncService = messageElasticSyncService;
    }

    /**
     * Sends a private chat message to the receiver.
     *
     * @param chatMessage The chat message to be sent.
     * @throws IOException If an error occurs while sending the message.
     */
    public void sendPrivateMessage(ChatMessagePayload chatMessage) throws IOException {

        // Retrieve the WebSocket session of the receiver in private chat
        WebSocketSession receiverSession = webSocketSessionManager.getUserSession(
                PRIVATE, chatMessage.getReceiver());

        ChatMessageDTO chatMessageDTO = MessageUtilityWrapper.chatMessageDTO(chatMessage);


        // Send the message if the session exists and is open
        if (receiverSession != null && receiverSession.isOpen()) {
            log.info("Sending message to {}: ", chatMessage.getReceiver());
            receiverSession.sendMessage(
                    new TextMessage(
                            objectMapper.writeValueAsString(chatMessageDTO)
                    )
            );
            messageElasticSyncService.syncMessageToElastic(chatMessageDTO, index);
        } else {
            log.info("user not connected storing message {}", chatMessage);
//            offlineMessageService.storeOfflineMessage(chatMessage.getReceiver(), chatMessage);

            /**
             * Fallback handling if receiverSession is null means receiver not connected
             * send to sender session about receiver not connected.
             */
//            WebSocketSession senderSession = webSocketSessionManager.getUserSession(
//                    PRIVATE,
//                    chatMessage.getSender()
//            );

//            if (senderSession != null && senderSession.isOpen()) {
//                chatMessage.setMessage("Receiver: "+ chatMessage.getReceiver()+", not connected.");
//                chatMessage.setStatus(STATUS.USER_NOT_CONNECTED);
//                senderSession.sendMessage(
//                        new TextMessage(
//                                objectMapper.writeValueAsString(chatMessage)
//                        )
//                );
//            }
        }
    }
}
