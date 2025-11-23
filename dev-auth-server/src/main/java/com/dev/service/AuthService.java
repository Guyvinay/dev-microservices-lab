package com.dev.service;

import com.dev.dto.RequestPasswordResetDto;
import com.dev.dto.ResetPasswordDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

import java.util.Map;

public interface AuthService {

    Map<String, String> login() throws JsonProcessingException, JOSEException;

    Map<String, String> requestPasswordReset(String url, RequestPasswordResetDto resetDto) throws MessagingException;

    Map<String, String> validateResetPassword(String email, String token);

    Map<String, String> resetPassword(@Valid ResetPasswordDto dto);

}
