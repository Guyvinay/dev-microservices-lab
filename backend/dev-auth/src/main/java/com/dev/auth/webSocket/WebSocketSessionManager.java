package com.dev.auth.webSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    // Stores active chat rooms: roomId -> (username -> WebSocketSession)
    private final Map<String, Map<String, WebSocketSession>> groupSessions = new ConcurrentHashMap<>();

    private void addUserToRoom(String username, WebSocketSession session) {
        groupSessions.computeIfAbsent(
                username,
                val -> new ConcurrentHashMap<>()
        ).put(username, session);
    }

    private void removeUserFromRoom(String roomId, String username) {
        Map<String, WebSocketSession> sessionMap = groupSessions.get(roomId);
        if(sessionMap != null) {
            sessionMap.remove(username);

            if(sessionMap.isEmpty()) {
                groupSessions.remove(roomId);
            }
        }
    }

    public Map<String, WebSocketSession> getUsersInRoom(String roomId) {
        return groupSessions.get(roomId);
    }

    public void addSession(String username, WebSocketSession session) {
        sessions.put(username, session);
    }

    public void removeSession(String username) {
        sessions.remove(username);
    }

    public WebSocketSession getWebSocketSession(String username) {
        return sessions.get(username);
    }

}
