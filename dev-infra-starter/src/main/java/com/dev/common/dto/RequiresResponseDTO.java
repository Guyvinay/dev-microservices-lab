package com.dev.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequiresResponseDTO {

    private String tenantId;
    private String userId;
    private Integer status;
    private String statusDesc;
    private Boolean allowed;
}
