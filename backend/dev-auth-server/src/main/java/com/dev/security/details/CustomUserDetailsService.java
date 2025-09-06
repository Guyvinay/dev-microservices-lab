package com.dev.security.details;

import com.dev.entity.UserProfileModel;
import com.dev.repository.UserProfileModelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserProfileModelRepository userProfileModelRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserProfileModel userProfileModel = userProfileModelRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: " + username));

        return new CustomUserDetails(
                userProfileModel.getEmail(),
                userProfileModel.getPassword(),
                "organization",
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
                Collections.emptyList()
        );
    }
}
