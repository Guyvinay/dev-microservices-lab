package com.dev.auth.webSocket.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageDTO {

    private String messageId;   // Unique message identifier
    private MessageType chatType;    // PRIVATE or GROUP

    // PRIVATE Chat Fields
    private String conversationId;  // Used for private chats (e.g., "userA-userB")
    private UserDTO receiver;       // Present only for private messages

    // GROUP Chat Fields
    private String roomId;          // Used for group chats
    private GroupDetailsDTO groupDetails;  // Group metadata

    private List<UserDTO> participants; // List of users in the conversation

    private UserDTO sender;  // Message sender

    private MessageDetailsDTO messageDetails; // Message content & attachments

    // Message Status
    private List<StatusDTO> status;  // Delivery & Read status (List for group, single object for private)

    private TimestampsDTO timestamps; // Sent, delivered, read timestamps

    private MetadataDTO metadata; // Flags for edit/delete
}

