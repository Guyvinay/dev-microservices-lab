package com.dev.auth.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebsocketConfiguration implements WebSocketConfigurer {
    /**
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
                .addHandler(
                        new WebSocketMessageHandler(),
                        "/chat/{chatId}/{userId}"
                )
                .addInterceptors(
                        new CustomHandshakeInterceptor()
                )
                .setAllowedOrigins("*");
    }
}
