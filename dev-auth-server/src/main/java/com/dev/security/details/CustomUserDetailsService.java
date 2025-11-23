package com.dev.security.details;

import com.dev.entity.UserProfileModel;
import com.dev.entity.UserProfileRoleMapping;
import com.dev.entity.UserProfileTenantMapping;
import com.dev.repository.OrganizationModelRepository;
import com.dev.repository.OrganizationTenantMappingRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.repository.UserProfileRoleMappingRepository;
import com.dev.repository.UserProfileTenantMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserProfileModelRepository userProfileModelRepository;

    private final OrganizationModelRepository organizationModelRepository;

    private final OrganizationTenantMappingRepository tenantMappingRepository;

    private final UserProfileTenantMappingRepository userProfileTenantMappingRepository;

    private final UserProfileRoleMappingRepository roleMappingRepository;

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

    public UserDetails loadUserByUsernameTenantAndOrg(String username) throws UsernameNotFoundException {

        UserProfileModel userProfileModel = userProfileModelRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        UserProfileTenantMapping userProfileTenantMapping = userProfileTenantMappingRepository.findByUserId(userProfileModel.getId()).getFirst();

        List<UserProfileRoleMapping> roles = roleMappingRepository.findByUserId(userProfileModel.getId());
        List<GrantedAuthority> roleId = roles.stream().map(role-> new SimpleGrantedAuthority(role.getRoleId().toString())).collect(Collectors.toList());

        return new CustomUserDetails(
                userProfileModel.getEmail(),
                userProfileModel.getPassword(),
                String.valueOf(userProfileTenantMapping.getOrganizationId()),
                userProfileTenantMapping.getTenantId(),
                roleId
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
