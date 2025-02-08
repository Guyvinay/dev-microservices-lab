package com.dev.auth.webSocket;

import com.dev.auth.dto.JwtTokenDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Retrieve authenticated user from the Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated
        if (authentication != null && authentication.getDetails() instanceof JwtTokenDto) {
            JwtTokenDto jwtTokenDto = (JwtTokenDto) authentication.getDetails();

            // Store username and additional user details in the WebSocket session attributes
            attributes.put("username", jwtTokenDto.getUsername());
            attributes.put("roles", jwtTokenDto.getRoles());  // Store user roles if needed

            return true; // Allow WebSocket connection
        }

        // Reject connection if authentication fails
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
