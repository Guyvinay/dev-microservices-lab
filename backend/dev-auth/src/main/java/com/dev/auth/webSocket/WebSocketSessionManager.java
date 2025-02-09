package com.dev.auth.webSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.dev.auth.utility.StringLiterals.PRIVATE;

/**
 * Manages WebSocket sessions for private and group chats.
 * <p>
 * This class maintains a mapping of active WebSocket sessions using a `roomId` as the key.
 * - "PRIVATE" is used as a key for private chat sessions.
 * - Group chats use the `roomId` as the key.
 * </p>
 */

@Component
public class WebSocketSessionManager {

    /**
     * Stores WebSocket sessions based on chat rooms.
     * - Key: Room ID (either "PRIVATE" for private chats or an actual room ID for group chats).
     * - Value: A map of usernames to their WebSocket sessions.
     */
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
     * Removes a user session from a specific chat room.
     * <p>
     * If the chat room becomes empty after removing the user, the room entry is also removed.
     * </p>
     *
     * @param roomId   ID of the chat room ("PRIVATE" or specific group chat ID).
     * @param username The username of the user to be removed.
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

    /**
     * Retrieves a user's WebSocket session from a specific chat room.
     *
     * @param roomId   ID of the chat room ("PRIVATE" or specific group chat ID).
     * @param username The username of the user whose session is being retrieved.
     * @return The WebSocket session if the user is present, otherwise null.
     */
    public WebSocketSession getUserSession(String roomId, String username) {
        if (roomId != null && username != null) {
            Map<String, WebSocketSession> usersSession = chatSessions.get(roomId); // 'PRIVATE' | {roomId};
            if (usersSession != null) {
                return usersSession.get(username);
            }
        }
        return null;
    }

    /**
     * Retrieves all active WebSocket sessions for a specific chat room.
     *
     * @param roomId ID of the chat room ("PRIVATE" or specific group chat ID).
     * @return A map of usernames to WebSocket sessions for the given chat room, or null if the room does not exist.
     */
    public Map<String, WebSocketSession> getChatRoomSessions(String roomId) {
        if (roomId != null) {
            return chatSessions.get(roomId); // 'PRIVATE' | {roomId};
        }
        return null;
    }
}
