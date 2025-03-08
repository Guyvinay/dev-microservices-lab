package com.dev.auth.repository;

import com.dev.auth.entity.UserProfileModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on UserProfileInfoModel.
 * This interface extends JpaRepository to provide standard methods for interacting with the database.
 */

public interface UserProfileModelRepository extends JpaRepository<UserProfileModel, UUID> {

    /**
     * Finds a UserProfileInfoModel by the email.
     * @param email - The email address.
     * @return Optional of UserProfileInfoModel.
     */
    Optional<UserProfileModel> findByEmail(String email);

    /**
     * Checks if an email already exists.
     * @param email - The email address.
     * @return true if email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
