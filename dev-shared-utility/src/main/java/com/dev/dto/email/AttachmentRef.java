package com.dev.dto.email;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentRef {

    private String fileName;
    private String contentType;

    // S3 / Minio / GCS path
    private String storagePath;

    private long sizeBytes;
}
