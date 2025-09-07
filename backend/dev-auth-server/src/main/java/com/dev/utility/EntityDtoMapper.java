package com.dev.utility;

import com.dev.dto.UserProfileRequestDTO;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.dto.UserProfileTenantDTO;
import com.dev.entity.UserProfileModel;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Objects;

@Component
public class EntityDtoMapper {

    public UserProfileModel toUserProfileModelEntity(UserProfileRequestDTO requestDTO) {
        return UserProfileModel.builder()
                .password(requestDTO.getPassword()) //hash password before saving
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .isActive(Objects.nonNull(requestDTO.getIsActive()) ? requestDTO.getIsActive() : false)
                .createdAt(Instant.now().toEpochMilli())
                .updatedAt(Instant.now().toEpochMilli())
                .build();
    }

    // Convert UserProfileModel to UserProfileResponseDTO
    public UserProfileResponseDTO toUserProfileResponseDTO(UserProfileModel userProfileModel) {
        return UserProfileResponseDTO.builder()
                .id(userProfileModel.getId())
                .email(userProfileModel.getEmail())
                .name(userProfileModel.getName())
                .isActive(userProfileModel.isActive())
                .createdAt(userProfileModel.getCreatedAt())
                .updatedAt(userProfileModel.getUpdatedAt())
                .build();
    }
}
