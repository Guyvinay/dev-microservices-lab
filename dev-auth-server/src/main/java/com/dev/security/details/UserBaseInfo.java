package com.dev.security.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserBaseInfo {

    private UUID id;
    private String email;
    private String name;
    private boolean isActive;
    private Long createdAt;
    private Long updatedAt;
    private String orgId;
    private String tenantId;
    private List<String> roleIds;

}
