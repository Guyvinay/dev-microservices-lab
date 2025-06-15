package com.dev.service;

import com.dev.dto.OrganizationTenantDTO;

import java.util.List;

public interface OrganizationTenantService {

    OrganizationTenantDTO createTenant(OrganizationTenantDTO dto);

    OrganizationTenantDTO getTenantById(String tenantId);

    List<OrganizationTenantDTO> getAllTenants();

    OrganizationTenantDTO updateTenant(String tenantId, OrganizationTenantDTO dto);

    void deleteTenant(String tenantId);
}