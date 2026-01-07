package com.dev.library.webSocket.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String sender;
    private String receiver;
    private String roomId;
    private String message;
    private MessageType type;
    private Long timeStamp;
    private STATUS status;
}
