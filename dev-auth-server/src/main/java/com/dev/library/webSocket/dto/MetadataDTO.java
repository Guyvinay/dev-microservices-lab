package com.dev.library.webSocket.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataDTO {
    private boolean edited;
    private boolean deleted;
    private String replyToMessageId;
    private List<ReactionDTO> reactions;
    private Long editedAt;
    private Long expiresAt;
    private ReactionCountsDTO reactionCounts;
}
