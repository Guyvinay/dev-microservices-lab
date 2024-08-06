package com.dev.common.dto.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentResponseDTO {
    private List<Document> documents;
    private Integer documentCount;
}
