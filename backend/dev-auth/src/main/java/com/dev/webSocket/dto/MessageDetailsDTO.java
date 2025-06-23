package com.dev.webSocket.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDetailsDTO {
    private String text;  // Message text
    private List<AttachmentDTO> attachments; // Optional attachments
}
