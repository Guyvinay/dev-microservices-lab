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
                .username(requestDTO.getUsername())
                .password(requestDTO.getPassword()) //hash password before saving
                .email(requestDTO.getEmail())
                .firstName(requestDTO.getFirstName())
                .lastName(requestDTO.getLastName())
                .isActive(Objects.nonNull(requestDTO.getIsActive()) ? requestDTO.getIsActive() : false)
                .updatedAt(System.currentTimeMillis())
                .build();
    }

    // Convert UserProfileModel to UserProfileResponseDTO
    public UserProfileResponseDTO toUserProfileResponseDTO(UserProfileModel userProfileModel) {
        return UserProfileResponseDTO.builder()
                .id(userProfileModel.getId())
                .username(userProfileModel.getUsername())
                .email(userProfileModel.getEmail())
                .firstName(userProfileModel.getFirstName())
                .lastName(userProfileModel.getLastName())
                .isActive(userProfileModel.isActive())
                .createdAt(userProfileModel.getCreatedAt())
                .updatedAt(userProfileModel.getUpdatedAt())
                .build();
    }
}
