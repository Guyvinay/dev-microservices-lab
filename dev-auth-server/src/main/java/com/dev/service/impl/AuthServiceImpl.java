package com.dev.service.impl;

import com.dev.bulk.email.service.EmailSendService;
import com.dev.dto.JwtTokenDto;
import com.dev.dto.RequestPasswordResetDto;
import com.dev.dto.ResetPasswordDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.entity.PasswordResetToken;
import com.dev.entity.UserProfileModel;
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

        JwtTokenDto jwtTokenDto = createJwtTokeDto(tokenDto, jwtExpiredIn);
        JWTRefreshTokenDto jwtRefreshTokenDto = createRefreshJwtTokeDto(tokenDto, refreshExpiredIn);
        tokensMap.put(JWT_TOKEN, jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtTokenDto), jwtExpiredIn));
        tokensMap.put(JWT_REFRESH_TOKEN, jwtTokenProviderManager.createJwtToken( new ObjectMapper().writeValueAsString(jwtRefreshTokenDto), refreshExpiredIn));

        return tokensMap;
    }

    @Override
    public Map<String, String> requestPasswordReset(String url, RequestPasswordResetDto resetDto) throws MessagingException {
        UserProfileResponseDTO profileResponseDTO = userProfileService.getUserByEmail(resetDto.getEmail());
        if (profileResponseDTO != null) {

            Optional<PasswordResetToken> resetTokenOptional = passwordResetTokenRepository.findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(resetDto.getEmail());
            if(resetTokenOptional.isPresent()) {
                PasswordResetToken token = resetTokenOptional.get();
                long now = Instant.now().toEpochMilli();
                long expiresAt = token.getExpiresAt();
                long remainingMillis = expiresAt - now;
//                if(remainingMillis > 0) {
//                    long remainingMinutes = TimeUnit.MILLISECONDS.toMinutes(remainingMillis);
//                    log.info("Request to reset password already present, try in {} minutes", remainingMinutes);
//                    throw new RuntimeException("Already requested for reset password. try after " + remainingMinutes + " minutes.");
//                }
            }

            String token = UUID.randomUUID().toString().replace("-", "") + RandomStringUtils.randomAlphanumeric(64);;
            String encodedToken = customBcryptEncoder.encode(token);
            long TOKEN_EXPIRY = Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .used(false)
                    .readyToUse(false)
                    .tokenHash(encodedToken)
                    .email(resetDto.getEmail())
                    .createdAt(Instant.now().toEpochMilli())
                    .expiresAt(TOKEN_EXPIRY)
                    .build();

            String resetLink = String.format("%s/dev-auth-server/api/auth/validate-reset-password?email=%s&token=%s", url, resetToken.getEmail(), token);
            emailSendService.sendPasswordResetEmail(resetToken.getEmail(), resetLink, profileResponseDTO.getName(), token);
            passwordResetTokenRepository.save(resetToken);
            return Map.of("response", "email sent, please check your email.");
        }
        return Map.of("response", "User not found");
    }

    @Override
    public Map<String, String> validateResetPassword(String email, String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(()-> new RuntimeException("No Password request found for this email: " + email));

        if (passwordResetToken.isUsed()) throw new RuntimeException("Token already used");

        if (Instant.ofEpochMilli(passwordResetToken.getExpiresAt()).isBefore(Instant.now()))
            throw new RuntimeException("Token expired: " + Instant.ofEpochMilli(passwordResetToken.getExpiresAt()));

        // Compare raw token with stored hash
        boolean matches = customBcryptEncoder.matches(token, passwordResetToken.getTokenHash());
        if (!matches) throw new RuntimeException("Invalid token");

        passwordResetToken.setReadyToUse(true);
        passwordResetTokenRepository.save(passwordResetToken);

        return Map.of("message", "token activate, ready to reset", "token", token);
    }

    @Override
    @Transactional
    public Map<String, String> resetPassword(ResetPasswordDto dto) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findFirstByEmailOrderByCreatedAtDesc(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("No password reset request found"));

        if (passwordResetToken.isUsed()) throw new RuntimeException("Token already used");

        if (!passwordResetToken.isReadyToUse()) throw new RuntimeException("Token is not validated, please first validate it.");

        if (Instant.ofEpochMilli(passwordResetToken.getExpiresAt()).isBefore(Instant.now()))
            throw new RuntimeException("Token expired: " + Instant.ofEpochMilli(passwordResetToken.getExpiresAt()));

        // Compare raw token with stored hash
        boolean matches = customBcryptEncoder.matches(dto.getToken(), passwordResetToken.getTokenHash());
        if (!matches) throw new RuntimeException("Invalid token");

        UserProfileModel user = userProfileModelRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        user.setPassword(customBcryptEncoder.encode(dto.getNewPassword()));
        userProfileModelRepository.save(user);

        // mark token used
        passwordResetToken.setUsed(true);
        passwordResetToken.setReadyToUse(false);
        passwordResetTokenRepository.save(passwordResetToken);

        return Map.of("status", "password reset successfully, you may login");
    }

    private JwtTokenDto createJwtTokeDto(JwtTokenDto userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());

        return new JwtTokenDto(
                userProfile.getUserId(),
                userProfile.getOrg(),
                userProfile.getName(),
                userProfile.getEmail(),
                userProfile.getTenantId(),
                createdDate,
                expiaryDate,
                userProfile.getRoles()
        );
    }

    private JWTRefreshTokenDto createRefreshJwtTokeDto(JwtTokenDto userProfile, int expiredIn) {
        ZonedDateTime zdt = LocalDateTime.now().atZone(ZoneOffset.UTC);
        Date createdDate = Date.from(ZonedDateTime.now(ZoneOffset.UTC).toInstant());
        Date expiaryDate = Date.from(zdt.plusMinutes(expiredIn).toInstant());
        return new JWTRefreshTokenDto(
                userProfile.getUserId(),
                userProfile.getOrg(),
                userProfile.getName(),
                userProfile.getEmail(),
                userProfile.getTenantId(),
                createdDate,
                expiaryDate,
                userProfile.getRoles()
        );
    }
}
