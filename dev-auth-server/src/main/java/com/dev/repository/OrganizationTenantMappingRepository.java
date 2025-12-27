package com.dev.repository;

import com.dev.entity.OrganizationTenantMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationTenantMappingRepository extends JpaRepository<OrganizationTenantMapping, String> {

    Optional<OrganizationTenantMapping> findByTenantName(String tenantName);

    Optional<OrganizationTenantMapping> findByTenantId(String tenantName);

    boolean existsByTenantName(String tenantName);
}