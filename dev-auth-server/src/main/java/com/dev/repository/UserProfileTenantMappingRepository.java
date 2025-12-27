package com.dev.repository;

import com.dev.entity.UserProfileTenantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserProfileTenantMappingRepository extends JpaRepository<UserProfileTenantMapping, UUID> {

    List<UserProfileTenantMapping> findByTenantId(String tenantId);

    List<UserProfileTenantMapping> findByUserId(UUID userId);

    boolean existsByTenantIdAndUserId(String tenantId, UUID userId);

    boolean existsByUserIdAndTenantId(UUID id, String aPublic);
}
