package com.dev.auth.service;

import com.dev.auth.dto.OrganizationDTO;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {

    OrganizationDTO createOrganization(OrganizationDTO dto);

    OrganizationDTO getOrganizationById(UUID orgId);

    List<OrganizationDTO> getAllOrganizations();

    OrganizationDTO updateOrganization(UUID orgId, OrganizationDTO dto);

    void deleteOrganization(UUID orgId);
}
