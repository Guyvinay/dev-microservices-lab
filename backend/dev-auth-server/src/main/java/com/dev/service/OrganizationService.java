package com.dev.service;

import com.dev.dto.OrgSignupRequestDTO;
import com.dev.dto.OrgSignupResponseDTO;
import com.dev.dto.OrganizationDTO;

import java.util.List;
import java.util.UUID;

public interface OrganizationService {

    OrganizationDTO createOrganization(OrganizationDTO dto);

    OrgSignupResponseDTO registerOrganizationWithDefaultTenant(OrgSignupRequestDTO dto);

    OrganizationDTO getOrganizationById(UUID orgId);

    List<OrganizationDTO> getAllOrganizations();

    OrganizationDTO updateOrganization(UUID orgId, OrganizationDTO dto);

    void deleteOrganization(UUID orgId);
}
