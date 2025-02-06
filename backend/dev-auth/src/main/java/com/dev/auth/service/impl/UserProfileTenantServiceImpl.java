package com.dev.auth.service.impl;

import com.dev.auth.dto.UserProfileTenantDTO;
import com.dev.auth.entity.UserProfileTenantMapping;
import com.dev.auth.exception.DuplicateResourceException;
import com.dev.auth.exception.ResourceNotFoundException;
import com.dev.auth.repository.UserProfileTenantRepository;
import com.dev.auth.service.UserProfileTenantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileTenantServiceImpl implements UserProfileTenantService {

    private final UserProfileTenantRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public UserProfileTenantDTO createMapping(UserProfileTenantDTO dto) {
        if (repository.existsByTenantIdAndUserId(dto.getTenantId(), dto.getUserId())) {
            throw new DuplicateResourceException("User is already mapped to this tenant.");
        }

        UserProfileTenantMapping mapping = modelMapper.map(dto, UserProfileTenantMapping.class);
        mapping = repository.save(mapping);
        return modelMapper.map(mapping, UserProfileTenantDTO.class);
    }

    @Override
    public UserProfileTenantDTO getMappingById(UUID id) {
        UserProfileTenantMapping mapping = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found."));
        return modelMapper.map(mapping, UserProfileTenantDTO.class);
    }

    @Override
    public List<UserProfileTenantDTO> getAllMappings() {
        return repository.findAll()
                .stream()
                .map(mapping -> modelMapper.map(mapping, UserProfileTenantDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfileTenantDTO> getMappingsByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId)
                .stream()
                .map(mapping -> modelMapper.map(mapping, UserProfileTenantDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserProfileTenantDTO> getMappingsByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapping -> modelMapper.map(mapping, UserProfileTenantDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteMapping(UUID id) {
        UserProfileTenantMapping mapping = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mapping not found."));
        repository.delete(mapping);
    }
}
