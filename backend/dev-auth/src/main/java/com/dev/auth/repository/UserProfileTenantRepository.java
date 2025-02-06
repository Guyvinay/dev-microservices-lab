package com.dev.auth.repository;

import com.dev.auth.entity.UserProfileTenantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProfileTenantRepository extends JpaRepository<UserProfileTenantMapping, UUID> {

    List<UserProfileTenantMapping> findByTenantId(String tenantId);

    List<UserProfileTenantMapping> findByUserId(UUID userId);

    boolean existsByTenantIdAndUserId(String tenantId, UUID userId);
}
