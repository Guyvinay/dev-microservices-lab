package com.dev.webSocket;

import com.dev.webSocket.messageService.GroupChatMessageService;
import com.dev.webSocket.messageService.OfflineMessageService;
import com.dev.webSocket.messageService.PrivateChatMessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * Configuration class for WebSocket setup.
 * <p>
 * This class enables WebSocket support in the Spring Boot application
 * and registers WebSocket handlers for private and group chats.
 * </p>
 */
@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private WebSocketSessionManager webSocketSessionManager;

    @Autowired
    private PrivateChatMessageService privateChatMessageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GroupChatMessageService groupChatMessageService;

    @Autowired
    private OfflineMessageService offlineMessageService;

    /**
     * Registers WebSocket handlers for private and group chat messaging.
     *
     * @param registry The WebSocket handler registry.
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(
                        new WebSocketMessageHandler(
                                webSocketSessionManager,
                                privateChatMessageService,
                                objectMapper,
                                groupChatMessageService,
                                offlineMessageService),
                "ws/chat/private",   // Private chat WebSocket endpoint
                        "ws/chat/group/{roomId}" // Group chat WebSocket endpoint
                )
                .addInterceptors(
                        new CustomHandshakeInterceptor()  // Interceptor for authentication/authorizatio
                )
                .setAllowedOrigins("*");// Allow all origins
    }
}
