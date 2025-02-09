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

/**
 * Custom WebSocket handshake interceptor that integrates with Spring Security.
 * Ensures that only authenticated users can establish a WebSocket connection.
 */
public class CustomHandshakeInterceptor implements HandshakeInterceptor {

    /**
     * Intercepts the WebSocket handshake request before the connection is established.
     * Retrieves the authenticated user's details from the Spring Security context and
     * stores the username in the WebSocket session attributes.
     *
     * @param request     The incoming handshake request.
     * @param response    The outgoing handshake response.
     * @param wsHandler   The WebSocket handler handling the connection.
     * @param attributes  A map of attributes to be stored in the WebSocket session.
     * @return {@code true} if the handshake should proceed; {@code false} if unauthorized.
     * @throws Exception If any error occurs during authentication validation.
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Retrieve authenticated user from the Spring Security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure the user is authenticated
        if (authentication != null && authentication.getDetails() instanceof JwtTokenDto) {
            JwtTokenDto jwtTokenDto = (JwtTokenDto) authentication.getDetails();

            // Store username and additional user details in the WebSocket session attributes
            attributes.put("username", jwtTokenDto.getUsername());

            return true; // Allow WebSocket connection
        }

        // Reject connection if authentication fails
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    /**
     * Intercepts the WebSocket handshake after it is completed.
     * Currently, no additional processing is needed.
     *
     * @param request   The handshake request.
     * @param response  The handshake response.
     * @param wsHandler The WebSocket handler.
     * @param exception Any exception that occurred during the handshake.
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
