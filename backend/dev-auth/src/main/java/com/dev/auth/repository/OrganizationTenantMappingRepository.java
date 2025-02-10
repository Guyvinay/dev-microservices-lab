package com.dev.auth.repository;

import com.dev.auth.entity.OrganizationTenantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationTenantMappingRepository extends JpaRepository<OrganizationTenantMapping, String> {

    Optional<OrganizationTenantMapping> findByTenantName(String tenantName);

    boolean existsByTenantName(String tenantName);
}