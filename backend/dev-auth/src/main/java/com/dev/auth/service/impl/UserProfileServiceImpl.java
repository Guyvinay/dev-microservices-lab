package com.dev.auth.service.impl;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;
import com.dev.auth.entity.UserProfileModel;
import com.dev.auth.exception.InvalidInputException;
import com.dev.auth.exception.UserNotFoundException;
import com.dev.auth.repository.UserProfileModelRepository;
import com.dev.auth.security.provider.CustomBcryptEncoder;
import com.dev.auth.service.UserProfileService;
import com.dev.auth.utility.EntityDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    // Repositories and mappers
    private final UserProfileModelRepository userProfileModelRepository;
    private final EntityDtoMapper entityDtoMapper;
    private final CustomBcryptEncoder customBcryptEncoder;

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
        profileModel.setPassword(customBcryptEncoder.encode(profileModel.getPassword()));
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
     * Retrieves the user profile by its unique ID.
     *
     * @param email The unique identifier of the user.
     * @return A DTO containing the user profile details.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public UserProfileResponseDTO getUserByEmail(String email) {
        if (Objects.isNull(email)) throw new InvalidInputException("User ID cannot be null");

        Optional<UserProfileModel> userProfile = userProfileModelRepository.findByEmail(email);

        if (userProfile.isEmpty()) {
            throw new UserNotFoundException("User not found with Email: " + email);
        }

        return entityDtoMapper.toUserProfileResponseDTO(userProfile.get());
    }

    /**
     * Retrieves the user profile by its unique ID.
     *
     * @param username The unique identifier of the user.
     * @return A DTO containing the user profile details.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
    public UserProfileResponseDTO getUserByUsername(String username) {
        if (Objects.isNull(username)) throw new InvalidInputException("User ID cannot be null");

        Optional<UserProfileModel> userProfile = userProfileModelRepository.findByUsername(username);

        if (userProfile.isEmpty()) {
            throw new UserNotFoundException("User not found with Username: " + username);
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
        existingUser.setName(request.getName());
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

    @Override
    public List<UserProfileResponseDTO> getAllUsers() {
        log.info("Fetching all user profiles");
        List<UserProfileModel> profiles = userProfileModelRepository.findAll();
        log.info("user profiles: {}", profiles.size());
        return profiles.stream().map(entityDtoMapper::toUserProfileResponseDTO).collect(Collectors.toList());
    }

    @Override
    public boolean existsByEmail(String email) {
        return userProfileModelRepository.existsByEmail(email);
    }
}
