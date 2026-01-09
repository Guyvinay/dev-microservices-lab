package com.dev.service.impl;

import com.dev.entity.JWTRefreshTokenEntity;
import com.dev.exception.AuthenticationException;
import com.dev.repository.JWTRefreshTokenRepository;
import com.dev.security.dto.AccessRefreshTokenDto;
import com.dev.security.dto.AccessJwtToken;
import com.dev.dto.RequestPasswordResetDto;
import com.dev.dto.ResetPasswordDto;
import com.dev.dto.UserProfileResponseDTO;
import com.dev.entity.PasswordResetToken;
import com.dev.entity.UserProfileModel;
import com.dev.exception.PasswordResetException;
import com.dev.repository.PasswordResetTokenRepository;
import com.dev.repository.UserProfileModelRepository;
import com.dev.security.dto.JwtToken;
import com.dev.security.provider.CustomBcryptEncoder;
import com.dev.security.provider.JwtTokenProviderManager;
import com.dev.service.AuthService;
import com.dev.service.UserProfileService;
import com.dev.utility.AuthContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.JOSEException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.dev.utility.DefaultConstants.TOKEN_EXPIRY_MINUTES;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProviderManager jwtTokenProviderManager;
    private final UserProfileService userProfileService;
    private final CustomBcryptEncoder customBcryptEncoder;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final AsyncEmailSendService asyncEmailSendService;
    private final UserProfileModelRepository userProfileModelRepository;
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;


    @Value("${security.jwt.refresh-expiry-minutes}")
    private int refreshExpiryMinutes;
    @Value("${security.jwt.access-expiry-minutes}")
    private int accessExpiryMinutes;

    /**
     * @return
     */
    @Override
    public AccessRefreshTokenDto login() throws JsonProcessingException, JOSEException {
        JwtToken tokenDto = AuthContextUtil.getJwtToken();

        AccessJwtToken accessDto =
                jwtTokenProviderManager.createAccessTokenDto(
                        tokenDto,
                        accessExpiryMinutes
                );

        AccessJwtToken refreshDto =
                jwtTokenProviderManager.createRefreshTokenDtoForLogin(
                        tokenDto,
                        refreshExpiryMinutes
                );
        storeRefreshToken(refreshDto);
        return new AccessRefreshTokenDto(
                jwtTokenProviderManager.createJwtToken(accessDto),
                jwtTokenProviderManager.createJwtToken(refreshDto)
        );
    }

    @Override
    public AccessRefreshTokenDto refresh() throws JOSEException, JsonProcessingException {
        JwtToken tokenDto = AuthContextUtil.getJwtToken();
        AccessJwtToken newAccessDto =
                jwtTokenProviderManager.createAccessTokenDto(
                        tokenDto,
                        accessExpiryMinutes
                );

        AccessJwtToken newRefreshDto =
                jwtTokenProviderManager.createRefreshTokenDtoForRefresh(
                        tokenDto
                );

        rotateToken(tokenDto.getJwtId(), newRefreshDto.getJwtId());

        storeRefreshToken(newRefreshDto);

        return new AccessRefreshTokenDto(
                jwtTokenProviderManager.createJwtToken(newAccessDto),
                jwtTokenProviderManager.createJwtToken(newRefreshDto)
        );
    }

    public void storeRefreshToken(AccessJwtToken dto) {
        JWTRefreshTokenEntity entity = JWTRefreshTokenEntity.builder()
                .jti(dto.getJwtId())
                .userId(dto.getUserBaseInfo().getId())
                .expiresAt(dto.getExpiresAt())
                .createdAt(dto.getCreatedAt())
                .revoked(false)
                .build();

        jwtRefreshTokenRepository.save(entity);
    }

    public JWTRefreshTokenEntity validateAndGet(UUID jti) {
        JWTRefreshTokenEntity token = jwtRefreshTokenRepository.findById(jti)
                .orElseThrow(() ->
                        new AuthenticationException("Invalid refresh token"));

        if (token.isRevoked()) {
            throw new AuthenticationException("Refresh token reused");
        }

        if (Instant.now().toEpochMilli() > token.getExpiresAt()) {
            throw new AuthenticationException("Refresh token expired");
        }

        return token;
    }

    public void rotateToken(
            UUID oldJti,
            UUID newJti
    ) {
        JWTRefreshTokenEntity oldToken = validateAndGet(oldJti);

        oldToken.setRevoked(true);
        oldToken.setRevokedAt(Instant.now().toEpochMilli());
        oldToken.setReplacedByJti(newJti);

        jwtRefreshTokenRepository.save(oldToken);
    }

    @Override
    public String requestPasswordReset(String url, RequestPasswordResetDto resetDto) throws MessagingException {
        UserProfileResponseDTO userProfileResponseDTO = userProfileService.getUserByEmail(resetDto.getEmail()); // silent check

        enforceResetCooldown(resetDto.getEmail());

        PasswordResetToken token = createResetToken(resetDto.getEmail());

        String resetLink = buildResetLink(url, token.getEmail(), token.getRawToken());

        asyncEmailSendService.sendPasswordResetEmail(
                token.getEmail(),
                resetLink,
                userProfileResponseDTO.getName(),
                token.getRawToken()
        );

        passwordResetTokenRepository.save(token);

        return "A password reset link has been sent.";
    }


    @Override
    @Transactional
    public String resetPassword(ResetPasswordDto dto) {

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

        return "Password reset successfully. You may now login.";
    }

    @Override
    public String validateResetPassword(String email, String token) {
        PasswordResetToken resetToken = getLatestValidToken(email);

        validateToken(resetToken, token);

        resetToken.setReadyToUse(true);
        passwordResetTokenRepository.save(resetToken);
        return "Token validated. You may now reset your password.";
    }

    private AccessJwtToken createRefreshJwtTokenDTO(AccessJwtToken tokenDto) {
        long expiresAt = tokenDto.getCreatedAt() + Duration.ofMinutes(refreshExpiryMinutes).toMillis();
        return AccessJwtToken.builder()
                .jwtId(tokenDto.getJwtId())
                .userBaseInfo(tokenDto.getUserBaseInfo())
                .tokenType(tokenDto.getTokenType())
                .createdAt(tokenDto.getCreatedAt())
                .expiresAt(expiresAt)
                .build();
    }

    private void enforceResetCooldown(String email) {

        passwordResetTokenRepository
                .findFirstByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .ifPresent(token -> {
                    long now = Instant.now().toEpochMilli();
                    if (token.getExpiresAt() > now) {
                        throw new PasswordResetException(
                                "Password reset already requested. Please try again later.");
                    }
                })
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
                .path("/api/auth/validate-reset-password")
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
