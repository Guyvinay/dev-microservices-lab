package com.dev.service.impl;

import com.dev.dto.UserProfileRequestDTO;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.dto.UserProfileTenantDTO;
import com.dev.dto.UserProfileTenantWrapper;
import com.dev.entity.UserProfileModel;
import com.dev.exception.InvalidInputException;
import com.dev.exception.UserNotFoundException;
import com.dev.rabbitmq.publisher.ReliableTenantPublisher;
import com.dev.redis.annotation.RedisCacheAdapter;
import com.dev.repository.UserProfileModelRepository;
import com.dev.security.provider.CustomBcryptEncoder;
import com.dev.service.UserProfileService;
import com.dev.service.UserProfileTenantService;
import com.dev.utility.EntityDtoMapper;
import com.dev.utility.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserProfileTenantService userProfileTenantService;
    private final ReliableTenantPublisher tenantPublisher;


    /**
     * Creates a new user profile.
     *
     * @param request The user profile request data.
     * @return A response DTO containing the created user profile data.
     * @throws InvalidInputException if the request is null or cannot be mapped.
     */
    @Override
    @Transactional
    public UserProfileTenantWrapper createUser(UserProfileRequestDTO request) {
        if (Objects.isNull(request)) throw new InvalidInputException("Request cannot be null");

        UserProfileModel profileModel = entityDtoMapper.toUserProfileModelEntity(request);

        if (Objects.isNull(profileModel)) {
            log.error("Entity mapping failed for request: {}", request);
            throw new InvalidInputException("Unable to convert request to user model");
        }

        profileModel.setPassword(customBcryptEncoder.encode(profileModel.getPassword()));

        log.info("Saving user profile for name: {}", profileModel.getName());
        UserProfileModel savedModel = userProfileModelRepository.save(profileModel);
        UserProfileResponseDTO savedProfileDto = entityDtoMapper.toUserProfileResponseDTO(savedModel);

        UserProfileTenantDTO profileTenantDTO = UserProfileTenantDTO.builder()
                .userId(savedModel.getId())
                .tenantId(request.getTenantId())
                .organizationId(request.getOrgId())
                .build();

        log.info("Creating tenant mapping for userId={} tenantId={} orgId={}",
                savedModel.getId(), request.getTenantId(), request.getOrgId());

        UserProfileTenantDTO savedProfileTenantMapping = userProfileTenantService.createMapping(profileTenantDTO);

        log.info("Tenant mapping created successfully: {}", savedProfileTenantMapping);
        log.info("User creation completed successfully for userId={}", savedModel.getId());

        return new UserProfileTenantWrapper(savedProfileDto, savedProfileTenantMapping);
    }

    /**
     * Fetches the user profile by its ID.
     *
     * @param userId The unique identifier of the user.
     * @return The user profile response DTO.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */

//    @Override
////    @Cacheable(value = "user", key = "#userId")
//    public UserProfileResponseDTO getUserById(UUID userId) {
//        log.info("Getting user info by id: {}", userId);
//        if (Objects.isNull(userId)) throw new InvalidInputException("User ID cannot be null");
//
//
//        UserProfileResponseDTO cachedUserProfile = cacheInspectorService.getValue(String.valueOf(userId), UserProfileResponseDTO.class);
//        if(cachedUserProfile!=null) {
//            return cachedUserProfile;
//        }
//
//        Optional<UserProfileModel> userProfile = userProfileModelRepository.findById(userId);
//
//        if (userProfile.isEmpty()) {
//            throw new UserNotFoundException("User not found with ID: " + userId);
//        }
//
//        log.info("User info retrieved successfully and returned");
//        UserProfileResponseDTO responseDTO = entityDtoMapper.toUserProfileResponseDTO(userProfile.get());
//        cacheInspectorService.setValue(String.valueOf(userId), responseDTO);
//        return responseDTO;
//    }

    /**
     * Fetches the user profile by its ID.
     *
     * @param id The unique identifier of the user.
     * @return The user profile response DTO.
     * @throws InvalidInputException if the ID is null.
     * @throws UserNotFoundException if no user is found with the given ID.
     */
    @Override
//    @Cacheable(cacheNames = "user", keyGenerator = "tenantAwareKeyGenerator")
    @RedisCacheAdapter(log = true)
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
    @RedisCacheAdapter(log = true)
    public UserProfileResponseDTO getUserByEmail(String email) {
        if (Objects.isNull(email)) throw new InvalidInputException("User ID cannot be null");

        Optional<UserProfileModel> userProfile = userProfileModelRepository.findByEmail(email);

        if (userProfile.isEmpty()) {
            throw new UserNotFoundException("User not found with Email: " + email);
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
    @RedisCacheAdapter(log = true)
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
