package com.dev.service;

import com.dev.dto.UserProfileRequestDTO;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.dto.UserProfileTenantWrapper;
import com.dev.exception.InvalidInputException;
import com.dev.exception.UserNotFoundException;

import java.util.List;
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
     * @throws InvalidInputException if the request is null or invalid.
     */
    UserProfileTenantWrapper createUser(UserProfileRequestDTO request);

    /**
     * Retrieves the user profile by its unique ID.
     *
     * @param id The unique identifier of the user.
     * @return A DTO containing the user profile details.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    UserProfileResponseDTO getUserById(UUID id);

    /**
     * Updates an existing user profile.
     *
     * @param id      The unique identifier of the user to be updated.
     * @param request The DTO containing the updated user profile details.
     * @return A DTO containing the updated user profile details.
     * @throws InvalidInputException if ID or request is null or invalid.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    UserProfileResponseDTO updateUser(UUID id, UserProfileRequestDTO request);

    /**
     * Retrieves the user profile by its unique ID.
     *
     * @param email The unique identifier of the user.
     * @return A DTO containing the user profile details.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    UserProfileResponseDTO getUserByEmail(String email);

    /**
     * Deletes a user profile by its unique ID.
     *
     * @param id The unique identifier of the user to be deleted.
     * @return A confirmation message after deletion.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    String deleteUser(UUID id);

    List<UserProfileResponseDTO> getAllUsers();

    boolean existsByEmail(String email);
}
