package com.dev.service;

import com.dev.dto.RequestPasswordResetDto;
import com.dev.dto.ResetPasswordDto;
import com.dev.security.dto.AccessRefreshTokenDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

public interface AuthService {

    AccessRefreshTokenDto login() throws JsonProcessingException, JOSEException;

    AccessRefreshTokenDto refresh() throws JOSEException, JsonProcessingException;

    String requestPasswordReset(String url, RequestPasswordResetDto resetDto) throws MessagingException;

    String validateResetPassword(String email, String token);

    String resetPassword(@Valid ResetPasswordDto dto);

}
