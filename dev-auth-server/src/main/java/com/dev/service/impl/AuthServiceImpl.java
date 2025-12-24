package com.dev.service.impl;

import com.dev.bulk.email.service.EmailSendService;
import com.dev.dto.JwtTokenDto;
import com.dev.dto.RequestPasswordResetDto;
import com.dev.dto.ResetPasswordDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.entity.PasswordResetToken;
import com.dev.entity.UserProfileModel;
import com.dev.exception.PasswordResetException;
import com.dev.repository.PasswordResetTokenRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.security.details.CustomAuthToken;
import com.dev.security.dto.JWTRefreshTokenDto;
import com.dev.security.provider.CustomBcryptEncoder;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.service.AuthService;
import com.dev.service.UserProfileService;
import com.dev.service.UserProfileTenantService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.dev.security.SecurityConstants.JWT_REFRESH_TOKEN;
import static com.dev.security.SecurityConstants.JWT_TOKEN;
import static com.dev.utility.DefaultConstants.TOKEN_EXPIRY_MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProviderManager jwtTokenProviderManager;
    private final UserProfileService userProfileService;
    private final CustomBcryptEncoder customBcryptEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailSendService emailSendService;
    private final UserProfileModelRepository userProfileModelRepository;

    /**
     * @return
     */
    @Override
    public Map<String, String> login() throws JsonProcessingException, JOSEException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        CustomAuthToken authToken = (CustomAuthToken) authentication;
        JwtTokenDto tokenDto = (JwtTokenDto) authToken.getDetails();

        int jwtExpiredIn = 2000000000;
        int refreshExpiredIn = 2000000000;
        Map<String, String> tokensMap = new HashMap<>();

        tokensMap.put(JWT_TOKEN, jwtTokenProviderManager.createJwtToken(new ObjectMapper().writeValueAsString(tokenDto), jwtExpiredIn));
        tokensMap.put(JWT_REFRESH_TOKEN, jwtTokenProviderManager.createJwtToken(new ObjectMapper().writeValueAsString(tokenDto), refreshExpiredIn));

        return tokensMap;
    }

    @Override
    public Map<String, String> requestPasswordReset(String url, RequestPasswordResetDto resetDto) throws MessagingException {
        UserProfileResponseDTO userProfileResponseDTO = userProfileService.getUserByEmail(resetDto.getEmail()); // silent check

        enforceResetCooldown(resetDto.getEmail());

        PasswordResetToken token = createResetToken(resetDto.getEmail());

        String resetLink = buildResetLink(url, token.getEmail(), token.getRawToken());

        emailSendService.sendPasswordResetEmail(
                token.getEmail(),
                resetLink,
                userProfileResponseDTO.getName(),
                token.getRawToken()
        );

        passwordResetTokenRepository.save(token);

        return genericResponse();
    }


    @Override
    @Transactional
    public Map<String, String> resetPassword(ResetPasswordDto dto) {

        PasswordResetToken token = getLatestValidToken(dto.getEmail());

        if (!token.isReadyToUse()) {
            throw new PasswordResetException("Token not validated");
        }

        validateToken(token, dto.getToken());

        UserProfileModel user = userProfileModelRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(() -> new PasswordResetException("User not found"));

        user.setPassword(customBcryptEncoder.encode(dto.getNewPassword()));
        userProfileModelRepository.save(user);

        invalidateToken(token);

        return Map.of("message", "Password reset successfully. You may now login.");
    }

    @Override
    public Map<String, String> validateResetPassword(String email, String token) {
        PasswordResetToken resetToken = getLatestValidToken(email);

        validateToken(resetToken, token);

        resetToken.setReadyToUse(true);
        passwordResetTokenRepository.save(resetToken);
        return Map.of("message", "Token validated. You may now reset your password.");
    }

    private Map<String, String> genericResponse() {
        return Map.of(
                "message",
                "If an account exists for this email, a password reset link has been sent."
        );
    }


    private void enforceResetCooldown(String email) {

        passwordResetTokenRepository
                .findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email)
//                .ifPresent(token -> {
//                    long now = Instant.now().toEpochMilli();
//                    if (token.getExpiresAt() > now) {
//                        throw new PasswordResetException(
//                                "Password reset already requested. Please try again later.");
//                    }
//                })
        ;
    }

    private PasswordResetToken createResetToken(String email) {

        String rawToken = UUID.randomUUID().toString().replace("-", "");
        String hashedToken = customBcryptEncoder.encode(rawToken);

        long now = Instant.now().toEpochMilli();
        long expiry = Instant.now()
                .plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES)
                .toEpochMilli();

        return PasswordResetToken.builder()
                .email(email)
                .tokenHash(hashedToken)
                .used(false)
                .readyToUse(false)
                .createdAt(now)
                .expiresAt(expiry)
                .rawToken(rawToken) // transient, NOT persisted
                .build();
    }

    private String buildResetLink(String baseUrl, String email, String token) {
        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/dev-auth-server/api/auth/validate-reset-password")
                .queryParam("email", email)
                .queryParam("token", token)
                .toUriString();
    }

    private void validateToken(PasswordResetToken token, String rawToken) {

        if (token.isUsed()) {
            throw new PasswordResetException("Token already used");
        }

        if (Instant.ofEpochMilli(token.getExpiresAt()).isBefore(Instant.now())) {
            throw new PasswordResetException("Token expired");
        }

        if (!customBcryptEncoder.matches(rawToken, token.getTokenHash())) {
            throw new PasswordResetException("Invalid token");
        }
    }

    private PasswordResetToken getLatestValidToken(String email) {
        return passwordResetTokenRepository
                .findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new PasswordResetException("No active password reset request found"));
    }

    private void invalidateToken(PasswordResetToken token) {
        token.setUsed(true);
        token.setReadyToUse(false);
        passwordResetTokenRepository.save(token);
    }

}
