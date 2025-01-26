package com.dev.auth.service;

import com.dev.auth.dto.UserProfileRequestDTO;
import com.dev.auth.dto.UserProfileResponseDTO;

import java.util.UUID;


/**
 * Interface for UserProfileService which defines methods for CRUD operations
 * on user profiles. Implementing classes will interact with user profile
 * repositories and data transfer objects (DTOs) for creating, updating,
 * retrieving, and deleting user profiles.
 */
public interface UserProfileService {
    /**
     * Creates a new user profile.
     *
     * @param request The DTO containing user profile details.
     * @return A DTO containing the created user profile details.
     * @throws com.dev.auth.exception.InvalidInputException if the request is null or invalid.
     */
    UserProfileResponseDTO createUser(UserProfileRequestDTO request);

    /**
     * Retrieves the user profile by its unique ID.
     *
     * @param id The unique identifier of the user.
     * @return A DTO containing the user profile details.
     * @throws com.dev.auth.exception.InvalidInputException if the ID is null.
     * @throws com.dev.auth.exception.UserNotFoundException if no user is found with the given ID.
     */
    UserProfileResponseDTO getUserById(UUID id);

    /**
     * Updates an existing user profile.
     *
     * @param id      The unique identifier of the user to be updated.
     * @param request The DTO containing the updated user profile details.
     * @return A DTO containing the updated user profile details.
     * @throws com.dev.auth.exception.InvalidInputException if ID or request is null or invalid.
     * @throws com.dev.auth.exception.UserNotFoundException if no user is found with the given ID.
     */
    UserProfileResponseDTO updateUser(UUID id, UserProfileRequestDTO request);

    /**
     * Deletes a user profile by its unique ID.
     *
     * @param id The unique identifier of the user to be deleted.
     * @return A confirmation message after deletion.
     * @throws com.dev.auth.exception.InvalidInputException if the ID is null.
     * @throws com.dev.auth.exception.UserNotFoundException if no user is found with the given ID.
     */
    String deleteUser(UUID id);
}
