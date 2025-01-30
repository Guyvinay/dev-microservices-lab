package com.dev.auth.service.impl;

import com.dev.auth.dto.LoginRequestDTO;
import com.dev.auth.security.details.CustomAuthToken;
import com.dev.auth.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * @param loginRequestDTO 
     * @return
     */
    @Override
    public String login(LoginRequestDTO loginRequestDTO) {
        Authentication authentication = new CustomAuthToken(
                loginRequestDTO.getOrgId(),
                loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword()
        );
        Authentication authenticated = authenticationManager.authenticate(authentication);

        SecurityContextHolder.getContext().setAuthentication(authenticated);

        return "login successful";
    }
}
