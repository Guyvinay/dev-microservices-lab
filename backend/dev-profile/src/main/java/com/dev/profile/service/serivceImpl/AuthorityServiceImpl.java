package com.dev.profile.service.serivceImpl;

import com.dev.profile.dto.AuthorityDTO;
import com.dev.profile.entity.Authority;
import com.dev.profile.exception.ResourceNotFoundException;
import com.dev.profile.repository.AuthorityRepository;
import com.dev.profile.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    // Create a new Authority
    @Override
    public AuthorityDTO createAuthority(AuthorityDTO authorityDTO) {
        Authority authority = authorityRepository.save(new Authority(authorityDTO.getName()));
        return new AuthorityDTO(authority.getId(), authority.getName());
    }

    // Update an existing Authority
    @Override
    public Authority updateAuthority(UUID id, Authority authorityDetails) {
        Authority existingAuthority = authorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Authority not found with id: " + id));
        existingAuthority.setName(authorityDetails.getName());
        return authorityRepository.save(existingAuthority);
    }

    // Get Authority by ID
    @Override
    public Authority getAuthorityById(UUID id) {
        return authorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Authority not found with id: " + id));
    }

    // Get all Authorities
    @Override
    public List<Authority> getAllAuthorities() {
        return authorityRepository.findAll();
    }

    // Delete an Authority by ID
    @Override
    public void deleteAuthority(UUID id) {
        Authority existingAuthority = authorityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Authority not found with id: " + id));
        authorityRepository.delete(existingAuthority);
    }

}
