package com.dev.auth.security.provider;

import com.dev.auth.entity.UserProfileModel;
import com.dev.auth.repository.UserProfileModelRepository;
import com.dev.auth.security.StatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserProfileModelRepository userRepository;

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProfileModel userProfileModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        return new CustomUserDetails(
                userProfileModel.getId(),
                userProfileModel.getUsername(),
                userProfileModel.getPassword(),
                userProfileModel.isActive(),
                getAuthorities(userProfileModel.getRoles())
        );
    }

    public UserDetails loadUserById(UUID id) {
        UserProfileModel user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.isActive(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }


    /**
     * Retrieves a collection of {@link org.springframework.security.core.GrantedAuthority} based on role
     * @param roleIds
     * @return a collection of {@link org.springframework.security.core.GrantedAuthority
     */
    private List<GrantedAuthority> getAuthorities(Set<String> roleIds) {
        return getGrantedAuthorities(getRoles(roleIds));
    }

    /**
     * Get all {@link GrantedAuthority based on assigned roles.. }
     * @param roles
     * @return
     */
    public List<GrantedAuthority> getGrantedAuthorities(List<String> roles) {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }

    /**
     * Roles based .. on having configuration
     * If role is AD then ROLE_USER & ROLE_ADMIN
     * @param rolesIds
     * @return
     */
    public List<String> getRoles(Set<String> rolesIds) {
        List<String> roles = new ArrayList<String>();
        for(String role : rolesIds) {
            if (role.toUpperCase().equalsIgnoreCase("AD")) {
                roles.add("ROLE_USER");
                roles.add("ROLE_ADMIN");
            } else{
                roles.add(role);
            }
        }
        return roles;
    }
}
