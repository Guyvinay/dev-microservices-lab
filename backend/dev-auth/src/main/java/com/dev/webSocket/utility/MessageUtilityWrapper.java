package com.dev.webSocket.utility;

import com.dev.webSocket.dto.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageUtilityWrapper {

    public static ChatMessageDTO chatMessageDTO(ChatMessagePayload chatMessage) {
        ChatMessageDTO.ChatMessageDTOBuilder chatMessageDTO = ChatMessageDTO.builder();

        // Generate unique message ID
        String messageId = UUID.randomUUID().toString();
        long currentTimestamp = Instant.now().toEpochMilli();

        // Extract sender and receiver
        String senderUsername = chatMessage.getSender();
        String receiverUsername = chatMessage.getReceiver();

        // Set common fields
        chatMessageDTO
                .messageId(messageId)
                .chatType(chatMessage.getChatType())
                .messageDetails(new MessageDetailsDTO(chatMessage.getMessage(), null))
                .timestamps(new TimestampsDTO(currentTimestamp, null, null))
                .sender(new UserDTO(senderUsername));

        // Handle Private Chat
        if (chatMessage.getChatType() == MessageType.PRIVATE) {
            chatMessageDTO
                    .receiver(new UserDTO(receiverUsername))  // Corrected receiver mapping
                    .conversationId(generateConversationId(senderUsername, receiverUsername))
                    .participants(List.of(
                            new UserDTO(senderUsername),
                            new UserDTO(receiverUsername)
                    ))
                    .status(List.of(new StatusDTO(receiverUsername, false, false)));  // Single status for private chat
        }

        // Handle Group Chat
        else if (chatMessage.getChatType() == MessageType.GROUP) {
            chatMessageDTO
                    .roomId(chatMessage.getGroupId())
                    .groupDetails(new GroupDetailsDTO(chatMessage.getGroupId(), chatMessage.getGroupId()))
//                    .participants(chatMessage.getGroupParticipants()) // Set participants
//                    .status(generateGroupStatus(chatMessage.getGroupParticipants())) // Status for each participant
            ;
        }

        MetadataDTO metadataDTO = new MetadataDTO();
        metadataDTO.setEdited(false);
        metadataDTO.setDeleted(false);
        metadataDTO.setReplyToMessageId(null);
        metadataDTO.setReactions(new ArrayList<>());
        metadataDTO.setEditedAt(null);
        metadataDTO.setExpiresAt(null);
        metadataDTO.setReactionCounts(new ReactionCountsDTO(0,0,0,0,0));

        // Metadata flags for edit/delete
        chatMessageDTO.metadata(metadataDTO);

        return chatMessageDTO.build();
    }

    // Helper method to generate a conversation ID for private chats
    private static String generateConversationId(String sender, String receiver) {
        return sender.compareTo(receiver) < 0 ? sender + "-" + receiver : receiver + "-" + sender;
    }

    // Helper method to generate status list for group messages
    private static List<StatusDTO> generateGroupStatus(List<UserDTO> participants) {
        return participants.stream()
                .map(user -> new StatusDTO(user.getUsername(), false, false))
                .toList();
    }
}
