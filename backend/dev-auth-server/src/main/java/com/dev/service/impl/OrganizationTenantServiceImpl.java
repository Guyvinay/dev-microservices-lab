package com.dev.service.impl;

import com.dev.dto.OrganizationTenantDTO;
import com.dev.entity.OrganizationTenantMapping;
import com.dev.exception.DuplicateResourceException;
import com.dev.exception.ResourceNotFoundException;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.service.OrganizationTenantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationTenantServiceImpl implements OrganizationTenantService {

    private final OrganizationTenantMappingRepository tenantRepository;
    private final ModelMapper modelMapper;

    @Override
    public OrganizationTenantDTO createTenant(OrganizationTenantDTO dto) {
        if (tenantRepository.existsByTenantName(dto.getTenantName())) {
            throw new DuplicateResourceException("Tenant name already exists.");
        }

        OrganizationTenantMapping tenant = modelMapper.map(dto, OrganizationTenantMapping.class);
        tenant.setCreatedAt(System.currentTimeMillis());
        tenant.setUpdatedAt(System.currentTimeMillis());

        tenant = tenantRepository.save(tenant);
        return modelMapper.map(tenant, OrganizationTenantDTO.class);
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
