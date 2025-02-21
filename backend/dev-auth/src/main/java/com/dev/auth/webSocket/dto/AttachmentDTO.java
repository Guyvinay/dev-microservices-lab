package com.dev.auth.webSocket.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentDTO {
    private String type; // IMAGE, VIDEO, etc.
    private String url;  // CDN URL or file location
}
