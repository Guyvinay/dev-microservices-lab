package com.dev.webSocket;

import com.dev.security.dto.JwtTokenDto;
import com.dev.utility.SecurityContextUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
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

        JwtTokenDto jwtTokenDto = SecurityContextUtil.getJwtTokenDtoFromContext();
        if(jwtTokenDto != null) {
            // Store username and additional user details in the WebSocket session attributes
            attributes.put("username", jwtTokenDto.getUserBaseInfo().getEmail());
            attributes.put("tenantId", jwtTokenDto.getUserBaseInfo().getTenantId());

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
