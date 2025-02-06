package com.dev.auth.service;

import com.dev.auth.dto.OrganizationTenantDTO;

import java.util.List;

public interface OrganizationTenantService {

    OrganizationTenantDTO createTenant(OrganizationTenantDTO dto);

    OrganizationTenantDTO getTenantById(String tenantId);

    List<OrganizationTenantDTO> getAllTenants();

    OrganizationTenantDTO updateTenant(String tenantId, OrganizationTenantDTO dto);

    void deleteTenant(String tenantId);
}