package com.dev.service.impl;

import com.dev.dto.OrganizationTenantDTO;
import com.dev.entity.OrganizationTenantMapping;
import com.dev.exception.DuplicateResourceException;
import com.dev.exception.ResourceNotFoundException;
import com.dev.rabbitmq.publisher.ReliableTenantPublisher;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.service.OrganizationTenantService;
import com.dev.utility.AuthUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganizationTenantServiceImpl implements OrganizationTenantService {

    private final OrganizationTenantMappingRepository tenantRepository;
    private final ModelMapper modelMapper;
    private final ReliableTenantPublisher tenantPublisher;

    @Override
    public OrganizationTenantDTO createTenant(OrganizationTenantDTO dto) {
        if (tenantRepository.existsByTenantName(dto.getTenantName())) {
            log.warn("Attempt to create duplicate tenant with name={}", dto.getTenantName());
            throw new DuplicateResourceException("Tenant name already exists.");
        }

        log.info("Creating new tenant with name={} for orgId={}", dto.getTenantName(), dto.getOrgId());

        OrganizationTenantMapping tenantMapping = new OrganizationTenantMapping();
        if(StringUtils.isNotBlank(dto.getTenantId())) {
            tenantMapping.setTenantId(dto.getTenantId());
        } else {
            long tenantId = AuthUtility.generateRandomNumber(5);
            tenantMapping.setTenantId(String.valueOf(tenantId));
        }
        tenantMapping.setOrgId(dto.getOrgId());
        tenantMapping.setTenantActive(true);
        tenantMapping.setCreatedAt(Instant.now().toEpochMilli());
        tenantMapping.setUpdatedAt(Instant.now().toEpochMilli());
        tenantMapping.setTenantName(dto.getTenantName());

        tenantMapping = tenantRepository.save(tenantMapping);

        log.info("Tenant created successfully: tenantId={} tenantName={} orgId={}",
                tenantMapping.getTenantId(), tenantMapping.getTenantName(), tenantMapping.getOrgId());
        tenantPublisher.publishTenantCreated(tenantMapping.getTenantId());
        return modelMapper.map(tenantMapping, OrganizationTenantDTO.class);
    }

    @Override
    public OrganizationTenantDTO getTenantById(String tenantId) {
        OrganizationTenantMapping tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));
        return modelMapper.map(tenant, OrganizationTenantDTO.class);
    }

    @Override
    public List<OrganizationTenantDTO> getAllTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(tenant -> modelMapper.map(tenant, OrganizationTenantDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrganizationTenantDTO updateTenant(String tenantId, OrganizationTenantDTO dto) {
        OrganizationTenantMapping tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));

        if (!tenant.getTenantName().equals(dto.getTenantName()) &&
                tenantRepository.existsByTenantName(dto.getTenantName())) {
            throw new DuplicateResourceException("Tenant name already in use.");
        }

        tenant.setTenantName(dto.getTenantName());
        tenant.setTenantActive(dto.isTenantActive());
        tenant.setUpdatedAt(System.currentTimeMillis());

        tenant = tenantRepository.save(tenant);
        return modelMapper.map(tenant, OrganizationTenantDTO.class);
    }

    @Override
    public void deleteTenant(String tenantId) {
        OrganizationTenantMapping tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found."));
        tenantRepository.delete(tenant);
    }
}
