package com.dev.webSocket.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessagePayload {
    private String receiver;
    private String sender;
    private String message;
    private MessageType chatType;
    private String groupId;
}
