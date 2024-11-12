package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfilingDocumentResponse {
    private List<ProfilingDocumentDTO> technicalProfiling;
    private Long totalCount;
}
