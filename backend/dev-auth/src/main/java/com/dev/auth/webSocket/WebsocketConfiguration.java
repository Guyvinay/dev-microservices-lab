package com.dev.auth.webSocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

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
    /**
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(
                        new WebSocketMessageHandler(
                                webSocketSessionManager,
                                privateChatMessageService,
                                objectMapper,
                                groupChatMessageService),
                "ws/chat/private",
                        "ws/chat/group/{roomId}"
                )
                .addInterceptors(
                        new CustomHandshakeInterceptor()
                )
                .setAllowedOrigins("*");
    }
}
