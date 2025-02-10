package com.dev.auth.service.impl;

import com.dev.auth.dto.OrgSignupRequestDTO;
import com.dev.auth.dto.OrganizationDTO;
import com.dev.auth.entity.OrganizationModel;
import com.dev.auth.entity.OrganizationTenantMapping;
import com.dev.auth.exception.DuplicateResourceException;
import com.dev.auth.exception.ResourceNotFoundException;
import com.dev.auth.repository.OrganizationModelRepository;
import com.dev.auth.repository.OrganizationTenantMappingRepository;
import com.dev.auth.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationModelRepository organizationRepository;
    private final OrganizationTenantMappingRepository organizationTenantMappingRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrganizationDTO createOrganization(OrganizationDTO dto) {
        if (organizationRepository.existsByOrgEmail(dto.getOrgEmail())) {
            throw new DuplicateResourceException("Organization email already exists.");
        }
        if (organizationRepository.existsByOrgName(dto.getOrgName())) {
            throw new DuplicateResourceException("Organization name already exists.");
        }

        OrganizationModel organization = modelMapper.map(dto, OrganizationModel.class);
        organization.setOrgId(UUID.randomUUID());
        organization.setCreatedAt(System.currentTimeMillis());
        organization.setUpdatedAt(System.currentTimeMillis());
        organization.setCreatedBy("SYSTEM");  // Ideally, get from security context
        organization.setUpdatedBy("SYSTEM");

        organization = organizationRepository.save(organization);
        return modelMapper.map(organization, OrganizationDTO.class);
    }

    /**
     * @param orgSignupRequestDTO
     * @return
     */
    @Override
    public OrganizationDTO registerOrganizationWithDefaultTenant(OrgSignupRequestDTO orgSignupRequestDTO) {

        OrganizationModel savedOrg = saveOrganization(orgSignupRequestDTO);
        OrganizationTenantMapping tenantMapping = saveOrgTenant(savedOrg.getOrgId(), orgSignupRequestDTO);
        return null;
    }

    private OrganizationTenantMapping saveOrgTenant(UUID orgId, OrgSignupRequestDTO orgSignupRequestDTO) {
        OrganizationTenantMapping tenantMapping = new OrganizationTenantMapping();
        return tenantMapping;
    }

    private OrganizationModel saveOrganization(OrgSignupRequestDTO orgSignupRequestDTO) {
        if(organizationRepository.existsByOrgEmail(orgSignupRequestDTO.getOrganizationEmail())) {
            throw new RuntimeException("Organization already exists with email: " + orgSignupRequestDTO.getOrganizationEmail());
        }

        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setOrgContact(orgSignupRequestDTO.getOrganizationContact());
        organizationModel.setOrgName(orgSignupRequestDTO.getOrganizationName());
        organizationModel.setOrgEmail(orgSignupRequestDTO.getOrganizationName());
        organizationModel.setCreatedAt(Instant.now().toEpochMilli());
        organizationModel.setUpdatedAt(Instant.now().toEpochMilli());
        organizationModel.setCreatedBy(orgSignupRequestDTO.getAdminEmail());
        organizationModel.setUpdatedBy(orgSignupRequestDTO.getAdminEmail());

        return organizationRepository.save(organizationModel);
    }

    @Override
    public OrganizationDTO getOrganizationById(UUID orgId) {
        OrganizationModel organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found."));
        return modelMapper.map(organization, OrganizationDTO.class);
    }

    @Override
    public List<OrganizationDTO> getAllOrganizations() {
        return organizationRepository.findAll()
                .stream()
                .map(org -> modelMapper.map(org, OrganizationDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationDTO updateOrganization(UUID orgId, OrganizationDTO dto) {

        OrganizationModel organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found."));

        if (!organization.getOrgEmail().equals(dto.getOrgEmail()) &&
                organizationRepository.existsByOrgEmail(dto.getOrgEmail())) {
            throw new DuplicateResourceException("Email already in use by another organization.");
        }

        organization.setOrgName(dto.getOrgName());
        organization.setOrgEmail(dto.getOrgEmail());
        organization.setOrgContact(dto.getOrgContact());
        organization.setUpdatedAt(System.currentTimeMillis());
        organization.setUpdatedBy("SYSTEM");

        organization = organizationRepository.save(organization);
        return modelMapper.map(organization, OrganizationDTO.class);
    }

    @Override
    public void deleteOrganization(UUID orgId) {
        OrganizationModel organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found."));
        organizationRepository.delete(organization);
    }
}
