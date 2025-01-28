package com.dev.auth.controller;

import com.dev.auth.dto.LoginRequestDTO;
import com.dev.auth.security.details.CustomAuthToken;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = new CustomAuthToken(
                loginRequestDTO.getUsername(),
                loginRequestDTO.getPassword(),
                loginRequestDTO.getOrgId()
        );

        Authentication authenticated = authenticationManager.authenticate(authentication);

        return ResponseEntity.ok("Login Successful for tenant: " + ((CustomAuthToken) authenticated).getOrgId());
    }


}
