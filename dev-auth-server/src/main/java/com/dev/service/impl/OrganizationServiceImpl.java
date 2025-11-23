package com.dev.service.impl;

import com.dev.dto.*;
import com.dev.entity.*;
import com.dev.exception.DuplicateResourceException;
import com.dev.exception.ResourceNotFoundException;
import com.dev.repository.*;
import com.dev.security.provider.CustomBcryptEncoder;
import com.dev.service.OrganizationService;
import com.dev.service.OrganizationTenantService;
import com.dev.service.UserProfileService;
import com.dev.service.UserProfileTenantService;
import com.dev.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.dev.utility.StringLiterals.ADMINISTRATOR;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationModelRepository organizationRepository;
    private final OrganizationTenantMappingRepository organizationTenantMappingRepository;
    private final OrganizationTenantService organizationTenantService;
    private final ModelMapper modelMapper;
    private final CustomBcryptEncoder customBcryptEncoder;
    private final UserProfileModelRepository userProfileModelRepository;
    private final UserProfileService userProfileService;
    private final UserProfileTenantMappingRepository userProfileTenantMappingRepository;
    private final UserProfileRoleModelRepository userProfileRoleModelRepository;
    private final UserProfileRoleMappingRepository userProfileRoleMappingRepository;
    private final UserProfileTenantService userProfileTenantService;


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
    @Transactional
    @Override
    public OrgSignupResponseDTO registerOrganizationWithDefaultTenant(OrgSignupRequestDTO orgSignupRequestDTO) {
        log.info("Starting organization registration process");
        OrganizationDTO savedOrg = saveOrganization(orgSignupRequestDTO);
//        uuidAtomicReference.set(savedOrg.getOrgId());
        OrganizationTenantDTO tenantMapping = saveOrgTenant(savedOrg.getOrgId(), orgSignupRequestDTO);
        UserProfileTenantWrapper adminUserTenant = createAdminUser(tenantMapping.getTenantId(), savedOrg.getOrgId(), orgSignupRequestDTO);
        UserProfileRoleModel userProfileRoleModel = createUserProfileAdminRole(adminUserTenant.getProfileResponseDTO().getId(), tenantMapping.getTenantId());
        UserProfileRoleMapping userProfileRoleMapping = createUserprofileRoleMapping(adminUserTenant.getProfileResponseDTO().getId(), userProfileRoleModel.getRoleId(), tenantMapping.getTenantId());
        log.info("Organization registration completed successfully");
        return new OrgSignupResponseDTO(savedOrg, tenantMapping, adminUserTenant, userProfileRoleModel, userProfileRoleMapping);
    }

    private UserProfileRoleMapping createUserprofileRoleMapping(UUID userId, Long roleId, String tenantId) {

        log.info("Mapping role ID: {} to user ID: {} in tenant ID: {}", roleId, userId, tenantId);

        UserProfileRoleMapping userProfileRoleMapping = new UserProfileRoleMapping();
        userProfileRoleMapping.setDefaultRole(true);
        userProfileRoleMapping.setTenantId(tenantId);
        userProfileRoleMapping.setUserId(userId);
        userProfileRoleMapping.setRoleId(roleId);

        UserProfileRoleMapping savedUserProfileRoleMapping = userProfileRoleMappingRepository.save(userProfileRoleMapping);

        log.info("User-role mapping saved successfully: {}", savedUserProfileRoleMapping.getId());

        return savedUserProfileRoleMapping;
    }

    private UserProfileRoleModel createUserProfileAdminRole(UUID userId, String tenantId) {
        log.info("Creating admin role for user ID: {} and tenant ID: {}", userId, tenantId);

        UserProfileRoleModel userProfileRoleModel = new UserProfileRoleModel();
        long roleId = AuthUtility.generateRandomNumber(8);
        userProfileRoleModel.setRoleId(roleId);
        userProfileRoleModel.setActive(true);
        userProfileRoleModel.setTenantId(tenantId);
        userProfileRoleModel.setRoleName(ADMINISTRATOR);
        userProfileRoleModel.setAdminFlag(true);
        userProfileRoleModel.setDescription("Administrator role created during organization creation");
        userProfileRoleModel.setCreatedAt(Instant.now().toEpochMilli());
        userProfileRoleModel.setUpdatedAt(Instant.now().toEpochMilli());
        userProfileRoleModel.setCreatedBy(String.valueOf(userId));
        userProfileRoleModel.setUpdatedBy(String.valueOf(userId));

        UserProfileRoleModel savedRole = userProfileRoleModelRepository.save(userProfileRoleModel);
        log.info("Admin role saved successfully with ID: {}", savedRole.getRoleId());
        return savedRole;
    }

    private UserProfileTenantDTO saveUserProfileTenantMapping(String tenantId, UUID userId, UUID orgId) {
        log.info("Mapping user ID: {} to tenant ID: {}", userId, tenantId);

        UserProfileTenantDTO profileTenantDTO = UserProfileTenantDTO.builder()
                .userId(userId)
                .tenantId(tenantId)
                .organizationId(orgId)
                .build();

        UserProfileTenantDTO savedUserProfileTenantMapping = userProfileTenantService.createMapping(profileTenantDTO);
        log.info("User-tenant mapping saved successfully: {}", savedUserProfileTenantMapping.getId());
        return savedUserProfileTenantMapping;
    }

    private UserProfileTenantWrapper createAdminUser(String tenantId, UUID orgId, OrgSignupRequestDTO orgSignupRequestDTO) {
        log.info("Creating admin user for tenant ID: {}", tenantId);

        UserProfileRequestDTO userProfileRequestDTO = new UserProfileRequestDTO();
        userProfileRequestDTO.setIsActive(true);
        userProfileRequestDTO.setName(orgSignupRequestDTO.getName());
        userProfileRequestDTO.setEmail(orgSignupRequestDTO.getAdminEmail());
        userProfileRequestDTO.setPassword(orgSignupRequestDTO.getAdminPassword());
        userProfileRequestDTO.setTenantId(tenantId);
        userProfileRequestDTO.setOrgId(orgId);

        UserProfileTenantWrapper savedUser = userProfileService.createUser(userProfileRequestDTO);
        log.info("Admin user created successfully with ID: {}", savedUser.getProfileResponseDTO().getId());
        return savedUser;
    }

    private OrganizationTenantDTO saveOrgTenant(UUID orgId, OrgSignupRequestDTO orgSignupRequestDTO) {
        log.info("Creating tenant for organization ID: {}, tenant: {}", orgId, orgSignupRequestDTO.getTenantName());

        OrganizationTenantDTO organizationTenantDTO = new OrganizationTenantDTO();
        String tenant = orgSignupRequestDTO.getTenantName();
        if(tenant.equalsIgnoreCase("public")) {
            organizationTenantDTO.setTenantId(tenant);
        } else {
            long tenantId = AuthUtility.generateRandomNumber(5);
            organizationTenantDTO.setTenantId(String.valueOf(tenantId));
        }
        organizationTenantDTO.setOrgId(orgId);
        organizationTenantDTO.setTenantName(tenant);
        organizationTenantDTO.setTenantActive(true);

        OrganizationTenantDTO savedTenant = organizationTenantService.createTenant(organizationTenantDTO);
        log.info("Tenant saved successfully with ID: {}", savedTenant.getTenantId());
        return savedTenant;
    }

    private OrganizationDTO saveOrganization(OrgSignupRequestDTO orgSignupRequestDTO) {
        log.info("Saving organization with email: {}", orgSignupRequestDTO.getOrganizationEmail());

        if (organizationRepository.existsByOrgEmail(orgSignupRequestDTO.getOrganizationEmail())) {
            throw new RuntimeException("Organization already exists with email: " + orgSignupRequestDTO.getOrganizationEmail());
        }

        OrganizationModel organizationModel = new OrganizationModel();
        organizationModel.setOrgContact(orgSignupRequestDTO.getOrganizationContact());
        organizationModel.setOrgName(orgSignupRequestDTO.getOrganizationName());
        organizationModel.setOrgEmail(orgSignupRequestDTO.getOrganizationEmail());
        organizationModel.setCreatedAt(Instant.now().toEpochMilli());
        organizationModel.setUpdatedAt(Instant.now().toEpochMilli());
        organizationModel.setCreatedBy(orgSignupRequestDTO.getAdminEmail());
        organizationModel.setUpdatedBy(orgSignupRequestDTO.getAdminEmail());

        OrganizationDTO savedOrg = modelMapper.map(organizationRepository.save(organizationModel), OrganizationDTO.class);
        log.info("Organization saved successfully with ID: {}", savedOrg.getOrgId());
        return savedOrg;
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
        int a = 3;
        int b = 4;


        OrganizationModel organization = organizationRepository.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization not found."));
        organizationRepository.delete(organization);
    }
}
