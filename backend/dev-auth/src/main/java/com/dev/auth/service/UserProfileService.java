package com.dev.auth.service;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;

import java.util.UUID;

public interface UserProfileService {

    UserProfileResponseDTO createUser(UserProfileRequestDTO request);

    UserProfileResponseDTO getUserById(UUID id);

    UserProfileResponseDTO updateUser(UUID id, UserProfileRequestDTO request);

    String deleteUser(UUID id);
}
