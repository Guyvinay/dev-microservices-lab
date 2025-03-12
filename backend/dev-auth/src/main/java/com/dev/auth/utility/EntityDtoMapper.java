package com.dev.auth.utility;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.entity.UserProfileModel;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class EntityDtoMapper {

    public UserProfileModel toUserProfileModelEntity(UserProfileRequestDTO requestDTO) {
        return UserProfileModel.builder()
                .password(requestDTO.getPassword()) //hash password before saving
                .email(requestDTO.getEmail())
                .name(requestDTO.getName())
                .isActive(Objects.nonNull(requestDTO.getIsActive()) ? requestDTO.getIsActive() : false)
                .updatedAt(System.currentTimeMillis())
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
