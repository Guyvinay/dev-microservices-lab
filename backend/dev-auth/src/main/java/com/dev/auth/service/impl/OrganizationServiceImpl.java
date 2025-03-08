package com.dev.auth.service.impl;

import com.dev.auth.dto.OrgSignupRequestDTO;
import com.dev.auth.dto.OrganizationDTO;
import com.dev.auth.entity.*;
import com.dev.auth.exception.DuplicateResourceException;
import com.dev.auth.exception.ResourceNotFoundException;
import com.dev.auth.repository.*;
import com.dev.auth.security.provider.CustomBcryptEncoder;
import com.dev.auth.service.OrganizationService;
import com.dev.auth.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dev.auth.utility.StringLiterals.ADMINISTRATOR;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationModelRepository organizationRepository;
    private final OrganizationTenantMappingRepository organizationTenantMappingRepository;
    private final ModelMapper modelMapper;
    private final CustomBcryptEncoder customBcryptEncoder;
    private final UserProfileModelRepository userProfileModelRepository;
    private final UserProfileTenantRepository userProfileTenantRepository;
    private final UserProfileRoleInfoRepository  userProfileRoleInfoRepository;


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
        UserProfileModel savedUser =  createAdminUser(tenantMapping.getTenantId(), orgSignupRequestDTO);
        UserProfileTenantMapping savedUserTenantMapping = saveUserProfileTenantMapping(tenantMapping.getTenantId(), savedUser.getId(), savedOrg.getOrgId());
        UserProfileRoleModel userProfileRoleModel = createUserProfileAdminRole(savedUser.getId(), tenantMapping.getTenantId());
        UserProfileRoleMapping userProfileRoleMapping = createUserprofileRoleMapping(savedUser.getId(), userProfileRoleModel.getRoleId(), tenantMapping.getTenantId());
        return null;
    }

    private UserProfileRoleMapping createUserprofileRoleMapping(UUID userId, Long roleId, String tenantId) {
        UserProfileRoleMapping userProfileRoleMapping = new UserProfileRoleMapping();
        userProfileRoleMapping.setDefaultRole(true);
        userProfileRoleMapping.setTenantId(tenantId);
        userProfileRoleMapping.setUserId(String.valueOf(userId));

        return userProfileRoleMapping;
    }

    private UserProfileRoleModel createUserProfileAdminRole(UUID userId, String tenantid) {
        UserProfileRoleModel userProfileRoleModel = new UserProfileRoleModel();
        userProfileRoleModel.setActive(true);
        userProfileRoleModel.setTenantId(tenantid);
        userProfileRoleModel.setRoleName(ADMINISTRATOR);
        userProfileRoleModel.setAdminFlag(true);
        userProfileRoleModel.setDescription("Administrator role created during organization creation");
        userProfileRoleModel.setCreatedAt(Instant.now().toEpochMilli());
        userProfileRoleModel.setUpdatedAt(Instant.now().toEpochMilli());
        userProfileRoleModel.setCreatedBy(String.valueOf(userId));
        userProfileRoleModel.setUpdatedBy(String.valueOf(userId));
        return userProfileRoleInfoRepository.save(userProfileRoleModel);
    }

    private UserProfileTenantMapping saveUserProfileTenantMapping(String tenantId, UUID userId, UUID orgId) {
        UserProfileTenantMapping tenantMapping = new UserProfileTenantMapping();
        tenantMapping.setOrganizationId(orgId);
        tenantMapping.setUserId(userId);
        tenantMapping.setTenantId(tenantId);
        return userProfileTenantRepository.save(tenantMapping);
    }

    private UserProfileModel createAdminUser(String tenantId, OrgSignupRequestDTO orgSignupRequestDTO) {
        UserProfileModel userProfileModel = new UserProfileModel();
        userProfileModel.setActive(true);
        userProfileModel.setCreatedAt(Instant.now().toEpochMilli());
        userProfileModel.setUpdatedAt(Instant.now().toEpochMilli());
        userProfileModel.setName(orgSignupRequestDTO.getName());
        userProfileModel.setEmail(orgSignupRequestDTO.getAdminEmail());
        userProfileModel.setPassword(customBcryptEncoder.encode(orgSignupRequestDTO.getAdminPassword()));

        return userProfileModelRepository.save(userProfileModel);
    }

    private OrganizationTenantMapping saveOrgTenant(UUID orgId, OrgSignupRequestDTO orgSignupRequestDTO) {
        OrganizationTenantMapping tenantMapping = new OrganizationTenantMapping();
        long tenantId = AuthUtility.generateRandomNumber(5);
        tenantMapping.setTenantId(String.valueOf(tenantId));
        tenantMapping.setOrgId(orgId);
        tenantMapping.setTenantActive(true);
        tenantMapping.setCreatedAt(Instant.now().toEpochMilli());
        tenantMapping.setUpdatedAt(Instant.now().toEpochMilli());
        tenantMapping.setTenantName(orgSignupRequestDTO.getTenantName());
        return organizationTenantMappingRepository.save(tenantMapping);
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
