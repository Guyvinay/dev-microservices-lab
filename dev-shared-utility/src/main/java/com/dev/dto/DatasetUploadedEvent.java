package com.dev.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasetUploadedEvent {
    private String tenantId;
    private String datasetId;
    private int batchNumber;
    private int totalBatches;
    private List<List<String>> rows;
}