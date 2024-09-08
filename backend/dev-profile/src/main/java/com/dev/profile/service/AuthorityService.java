package com.dev.profile.service;

import com.dev.profile.dto.AuthorityDTO;
import com.dev.profile.entity.Authority;

import java.util.List;
import java.util.UUID;

public interface AuthorityService {

    // Create a new Authority
    public AuthorityDTO createAuthority(AuthorityDTO authority);

    // Update an existing Authority
    public Authority updateAuthority(UUID id, Authority authorityDetails);

    // Get Authority by ID
    public Authority getAuthorityById(UUID id);

    // Get all Authorities
    public List<Authority> getAllAuthorities();

    // Delete an Authority by ID
    public void deleteAuthority(UUID id);

}
