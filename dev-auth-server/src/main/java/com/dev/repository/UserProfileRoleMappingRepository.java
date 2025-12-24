package com.dev.repository;

import com.dev.entity.UserProfileRoleMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserProfileRoleMappingRepository extends JpaRepository<UserProfileRoleMapping, UUID> {

    // Find all roles assigned to a specific user
    List<UserProfileRoleMapping> findByUserId(UUID userId);

    // Find default role of a user (per tenant)
    Optional<UserProfileRoleMapping> findByUserIdAndTenantIdAndDefaultRoleTrue(UUID userId, String tenantId);

    // Check if user already has this role
    boolean existsByUserIdAndRoleId(UUID userId, Long roleId);

    // Find by user + role
    Optional<UserProfileRoleMapping> findByUserIdAndRoleId(UUID userId, Long roleId);

    boolean existsByUserId(UUID id);
}
