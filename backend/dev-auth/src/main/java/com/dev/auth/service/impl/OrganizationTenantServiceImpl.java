package com.dev.auth.service.impl;

import com.dev.auth.dto.OrganizationTenantDTO;
import com.dev.auth.entity.OrganizationTenantMapping;
import com.dev.auth.exception.DuplicateResourceException;
import com.dev.auth.exception.ResourceNotFoundException;
import com.dev.auth.repository.OrganizationTenantRepository;
import com.dev.auth.service.OrganizationTenantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationTenantServiceImpl implements OrganizationTenantService {

    private final OrganizationTenantRepository tenantRepository;
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
