package com.dev.auth.webSocket.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetadataDTO {
    private boolean edited;
    private boolean deleted;
}
