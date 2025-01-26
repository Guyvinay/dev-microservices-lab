package com.dev.auth.service.impl;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.entity.UserProfileModel;
import com.dev.auth.exception.InvalidInputException;
import com.dev.auth.exception.UserNotFoundException;
import com.dev.auth.repository.UserProfileModelRepository;
import com.dev.auth.service.UserProfileService;
import com.dev.auth.utility.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the UserProfileService interface that handles
 * CRUD operations related to user profiles. This service interacts
 * with the database via the UserProfileModelRepository to perform
 * the necessary operations and uses EntityDtoMapper for mapping
 * between entities and DTOs.
 * <p>
 * This service throws appropriate exceptions in case of invalid inputs
 * or when the user is not found in the database.
 *
 * @service UserProfileServiceImpl
 */
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    // Repositories and mappers
    private final UserProfileModelRepository userProfileModelRepository;
    private final EntityDtoMapper entityDtoMapper;

    /**
     * Creates a new user profile.
     *
     * @param request The user profile request data.
     * @return A response DTO containing the created user profile data.
     * @throws InvalidInputException if the request is null or cannot be mapped.
     */
    @Override
    public UserProfileResponseDTO createUser(UserProfileRequestDTO request) {
        if (Objects.isNull(request)) throw new InvalidInputException("Request cannot be null");

        UserProfileModel profileModel = entityDtoMapper.toUserProfileModelEntity(request);

        if (Objects.isNull(profileModel)) throw new InvalidInputException("Unable to convert request to user model");

        UserProfileModel savedModel = userProfileModelRepository.save(profileModel);
        return entityDtoMapper.toUserProfileResponseDTO(savedModel);
    }

    /**
     * Fetches the user profile by its ID.
     *
     * @param id The unique identifier of the user.
     * @return The user profile response DTO.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public UserProfileResponseDTO getUserById(UUID id) {
        if (Objects.isNull(id)) throw new InvalidInputException("User ID cannot be null");

        Optional<UserProfileModel> userProfile = userProfileModelRepository.findById(id);

        if (userProfile.isEmpty()) {
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        return entityDtoMapper.toUserProfileResponseDTO(userProfile.get());
    }

    /**
     * Updates the user profile.
     *
     * @param id      The unique identifier of the user.
     * @param request The updated user profile data.
     * @return The updated user profile response DTO.
     * @throws InvalidInputException if ID or request is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public UserProfileResponseDTO updateUser(UUID id, UserProfileRequestDTO request) {
        if (id == null || request == null) {
            throw new InvalidInputException("ID or request cannot be null");
        }

        // Check if user exists
        UserProfileModel existingUser = userProfileModelRepository.findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with ID: " + id)
                );
        // Map request to existing model
        existingUser.setUsername(request.getUsername());
        existingUser.setEmail(request.getEmail());
        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setActive(request.getIsActive());

        UserProfileModel updatedModule = userProfileModelRepository.save(existingUser);

        return entityDtoMapper.toUserProfileResponseDTO(updatedModule);
    }

    /**
     * Deletes a user profile.
     *
     * @param id The unique identifier of the user.
     * @return A confirmation message.
     * @throws InvalidInputException if ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public String deleteUser(UUID id) {

        if (Objects.isNull(id)) throw new InvalidInputException("User ID cannot be null");

        // Check if user exists
        if (!userProfileModelRepository.existsById(id))
            throw new UserNotFoundException("User not found with ID: " + id);

        userProfileModelRepository.deleteById(id);
        return "User profile deleted: " + id;
    }
}
