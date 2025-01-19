package com.dev.auth.repository;

import com.dev.auth.entity.UserProfileInfoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for performing CRUD operations on UserProfileInfoModel.
 * This interface extends JpaRepository to provide standard methods for interacting with the database.
 */

@Repository
public interface UserProfileInfoRepository extends JpaRepository<UserProfileInfoModel, UUID> {
    /**
     * Finds a UserProfileInfoModel by the username.
     * @param username - The username.
     * @return Optional of UserProfileInfoModel.
     */
    Optional<UserProfileInfoModel> findByUsername(String username);

    /**
     * Finds a UserProfileInfoModel by the email.
     * @param email - The email address.
     * @return Optional of UserProfileInfoModel.
     */
    Optional<UserProfileInfoModel> findByEmail(String email);

    /**
     * Checks if a username already exists.
     * @param username - The username.
     * @return true if username exists, false otherwise.
     */
    boolean existsByUsername(String username);

    /**
     * Checks if an email already exists.
     * @param email - The email address.
     * @return true if email exists, false otherwise.
     */
    boolean existsByEmail(String email);
}
