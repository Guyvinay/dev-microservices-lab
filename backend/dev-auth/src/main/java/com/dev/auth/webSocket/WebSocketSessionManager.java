package com.dev.auth.webSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dev.auth.utility.StringLiterals.PRIVATE;

@Component
public class WebSocketSessionManager {
    //* Map to store with roomId ('PRIVATE' | {roomId}).
    private final Map<String, Map<String, WebSocketSession>> chatSessions = new ConcurrentHashMap<>();

    /**
     * Adds the user to the map with roomId ('PRIVATE' | {roomId}).
     *
     * @param roomId   id of the chat either 'PRIVATE' for private or roomId for group chat.
     * @param username id of the user that is sending the message either in PRIVATE or {groupID}.
     * @param session  session to be added with username either in PRIVATE or with roomId.
     */
    public void addUserSession(String roomId, String username, WebSocketSession session) {
        chatSessions.computeIfAbsent(
                roomId,
                val -> new ConcurrentHashMap<>()
        ).put(username, session);
    }

    public void addUserToPrivateChat(String username, WebSocketSession session) {
        chatSessions.computeIfAbsent(
                PRIVATE,
                val -> new ConcurrentHashMap<>()
        ).put(username, session);
    }

    /**
     * @param roomId
     * @param username
     */
    public void removeUserFromChat(String roomId, String username) {
        Map<String, WebSocketSession> chatRoom = chatSessions.get(roomId);
        if (chatRoom != null) {
            chatRoom.remove(username);
            if (chatRoom.isEmpty()) {
                chatSessions.remove(roomId);
            }
        }
    }

    public WebSocketSession getUserSession(String roomId, String username) {
        if (roomId != null && username != null) {
            Map<String, WebSocketSession> usersSession = chatSessions.get(roomId); // 'PRIVATE' | {roomId};
            if (usersSession != null) {
                return usersSession.get(username);
            }
        }
        return null;
    }

    public Map<String, WebSocketSession> getChatRoomSessions(String roomId) {
        if (roomId != null) {
            return chatSessions.get(roomId); // 'PRIVATE' | {roomId};
        }
        return null;
    }
}
