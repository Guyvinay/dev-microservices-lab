package com.dev.security.details;

import com.dev.entity.OrganizationModel;
import com.dev.entity.OrganizationTenantMapping;
import com.dev.entity.UserProfileModel;
import com.dev.repository.OrganizationModelRepository;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.repository.UserProfileModelRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserProfileModelRepository userProfileModelRepository;

    @Autowired
    private OrganizationModelRepository organizationModelRepository;

    @Autowired
    private OrganizationTenantMappingRepository tenantMappingRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserProfileModel userProfileModel = userProfileModelRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        return new CustomUserDetails(
                userProfileModel.getEmail(),
                userProfileModel.getPassword(),
                "organization",
                "tenantId",
                Collections.emptyList()
        );
    }

    public UserDetails loadUserByUsernameTenantAndOrg(String username, String tenantId, String org) throws UsernameNotFoundException {

        OrganizationModel organizationModel = organizationModelRepository.findById(UUID.fromString(org)).orElseThrow(
                ()-> new UsernameNotFoundException("Org not found")
        );
        OrganizationTenantMapping tenantMapping = tenantMappingRepository.findById(tenantId).orElseThrow(
                ()-> new UsernameNotFoundException("Tenant not found")
        );

        if(
                !StringUtils.equals(
                    String.valueOf(organizationModel.getOrgId()),
                    String.valueOf(tenantMapping.getOrgId())
                )
        ) throw new UsernameNotFoundException("Tenant org mismatch");

        UserProfileModel userProfileModel = userProfileModelRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        return new CustomUserDetails(
                userProfileModel.getEmail(),
                userProfileModel.getPassword(),
                String.valueOf(organizationModel.getOrgId()),
                tenantMapping.getTenantId(),
                Collections.emptyList()
        );
    }

    public UserDetails loadUserByEmail(String username) throws UsernameNotFoundException {

        UserProfileModel userProfileModel = userProfileModelRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found: " + username));

        return new CustomUserDetails(
                userProfileModel.getEmail(),
                userProfileModel.getPassword(),
                "organization",
                "tenantId",
                Collections.emptyList()
        );
    }
}
