package com.dev.dto;

import com.dev.entity.UserProfileModel;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserProfileDetailsDto {

    private UUID userId;
    private String email;
    private String name;
    private Boolean isActive;
    private UUID orgId;
    private String tenantId;
    private List<String> roleIds;

    public UserProfileDetailsDto(UserProfileModel profileModel) {
        this.userId = profileModel.getId();
        this.email = profileModel.getEmail();
        this.name = profileModel.getName();
        this.isActive = profileModel.isActive();
    }
}
