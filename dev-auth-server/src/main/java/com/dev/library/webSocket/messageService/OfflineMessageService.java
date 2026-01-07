package com.dev.library.webSocket.messageService;

import com.dev.library.webSocket.dto.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OfflineMessageService {
    private final Map<String, Queue<ChatMessage>> offlineMessages = new ConcurrentHashMap<>();

    public void storeOfflineMessage(String username, ChatMessage message) {
        offlineMessages.computeIfAbsent(username, key -> new LinkedList<>()).add(message);
    }

    public Queue<ChatMessage> getOfflineMessages(String username) {
        return offlineMessages.getOrDefault(username, new LinkedList<>());
    }

    public void removeOfflineMessages(String username) {
        offlineMessages.remove(username);
    }

}
