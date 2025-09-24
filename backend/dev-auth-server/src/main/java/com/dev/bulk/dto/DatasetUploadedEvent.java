package com.dev.bulk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DatasetUploadedEvent {
    private String tenantId;
    private String datasetId;
    private int batchNumber;
    private int totalBatches;
    private List<Map<String, String>> rows;
}
