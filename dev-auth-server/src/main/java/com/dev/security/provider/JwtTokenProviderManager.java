package com.dev.security.provider;

import com.dev.dto.UserProfileDetailsDto;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.dto.JwtTokenDto;
import com.dev.exception.AuthenticationException;
import com.dev.exception.JWTTokenException;
import com.dev.security.details.CustomAuthToken;
import com.dev.security.dto.TokenType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.dev.security.utility.SecurityConstants.AUDIENCE;
import static com.dev.security.utility.SecurityConstants.AUTHORIZATION;
import static com.dev.security.utility.SecurityConstants.AUTHZ;
import static com.dev.security.utility.SecurityConstants.AUTH_TYPE;
import static com.dev.security.utility.SecurityConstants.CLOCK_SKEW_SECONDS;
import static com.dev.security.utility.SecurityConstants.ISSUER;
import static com.dev.security.utility.SecurityConstants.MAX_TOKEN_SIZE;
import static com.dev.security.utility.SecurityConstants.PERMISSION;
import static com.dev.security.utility.SecurityConstants.SIGNING_SECRET_KEY;

@Slf4j
@Component
public class JwtTokenProviderManager {

    private JWSSigner reqSigner;
    private JWSVerifier jwsVerifier;

    private final ObjectMapper OM = new ObjectMapper();

    @PostConstruct
    protected void postConstruct() throws JOSEException {
        reqSigner = new MACSigner(SIGNING_SECRET_KEY.getBytes());
        jwsVerifier = new MACVerifier(SIGNING_SECRET_KEY.getBytes());
    }

    public String createJwtToken(
            JwtTokenDto jwtTokenDto
    ) throws JOSEException, JsonProcessingException {

        validateCreateJwtToken(jwtTokenDto);

        Instant issuedAt = Instant.ofEpochMilli(jwtTokenDto.getCreatedAt());
        Instant expiresAt = Instant.ofEpochMilli(jwtTokenDto.getExpiresAt());

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .jwtID(jwtTokenDto.getJwtId().toString())
                .subject(OM.writeValueAsString(jwtTokenDto))
                .issuer(ISSUER)
                .audience(AUDIENCE)
                .issueTime(Date.from(issuedAt))
                .notBeforeTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .claim(AUTH_TYPE, jwtTokenDto.getTokenType().name())
                .claim(PERMISSION, AUTHZ)
                .build();

        SignedJWT signedJWT = new SignedJWT(buildJwsHeader(), claims);
        signedJWT.sign(reqSigner);

        return signedJWT.serialize();
    }

    private static void validateCreateJwtToken(JwtTokenDto jwtTokenDto) {
        // Validate required fields
        if (jwtTokenDto.getCreatedAt() <= 0) {
            throw new IllegalArgumentException("createdAt must be set");
        }

        if (jwtTokenDto.getExpiresAt() <= jwtTokenDto.getCreatedAt()) {
            throw new IllegalArgumentException("expiresAt must be greater than createdAt");
        }

        if (jwtTokenDto.getTokenType() == null) {
            throw new IllegalArgumentException("tokenType must be set");
        }

        // ️Ensure JWT ID
        if (jwtTokenDto.getJwtId() == null) {
            jwtTokenDto.setJwtId(UUID.randomUUID());
        }
    }

    private JWSHeader buildJwsHeader() {
        return new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(AUTHORIZATION);
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public JWTClaimsSet resolveAndValidateToken(String token, TokenType access) throws ParseException, JOSEException {
        validateTokenSize(token);

        SignedJWT signedJWT = SignedJWT.parse(token);

        verifySignature(signedJWT);

        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

        validateStandardClaims(claims);
        validateIssuer(claims);
        validateAudience(claims);
        validateTokenType(claims, access);
        return claims;
    }
    private void validateTokenType(
            JWTClaimsSet claims,
            TokenType expectedType
    ) throws ParseException {

        String tokenType = claims.getStringClaim(AUTH_TYPE);

        if (!StringUtils.hasText(tokenType)) {
            throw new AuthenticationException("Missing token type");
        }

        if (!expectedType.name().equals(tokenType)) {
            throw new AuthenticationException(
                    "Invalid token type. Expected: " + expectedType);
        }
    }

    private void validateIssuer(JWTClaimsSet claims) {
        if (!ISSUER.equals(claims.getIssuer())) {
            log.warn("Invalid JWT issuer: {}", claims.getIssuer());
            throw new AuthenticationException("Invalid token issuer");
        }
    }

    private void validateStandardClaims(JWTClaimsSet claims) throws JWTTokenException {

        Instant now = Instant.now();

        if (claims.getExpirationTime() == null) {
            throw new JWTTokenException("Missing exp claim");
        }

        Instant exp = claims.getExpirationTime().toInstant();
        if (exp.isBefore(now.minusSeconds(CLOCK_SKEW_SECONDS))) {
            throw new AuthenticationException("JWT expired");
        }

        Date notBefore = claims.getNotBeforeTime();
        if (notBefore != null &&
                notBefore.toInstant().isAfter(now.plusSeconds(CLOCK_SKEW_SECONDS))) {
            throw new AuthenticationException("JWT not active yet");
        }
    }

    private void verifySignature(SignedJWT signedJWT) throws JOSEException {
        if (!signedJWT.verify(jwsVerifier)) {
            log.warn("JWT signature verification failed");
            throw new JWTTokenException("Invalid JWT signature");
        }
    }
    private void validateAudience(JWTClaimsSet claims) {

        List<String> tokenAudiences = claims.getAudience();

        if (CollectionUtils.isEmpty(tokenAudiences)) {
            throw new AuthenticationException("Missing token audience");
        }

        if (Collections.disjoint(tokenAudiences, AUDIENCE)) {
            throw new AuthenticationException("Invalid token audience");
        }
    }

    private void validateTokenSize(String token) throws JWTTokenException {
        if (token.getBytes(StandardCharsets.UTF_8).length > MAX_TOKEN_SIZE) {
            throw new JWTTokenException("JWT too large");
        }
    }

    /**
     * Extracts user details from a valid JWT.
     */
    public String getSubjectPayload(String token, TokenType access) throws JOSEException, ParseException {
        JWTClaimsSet claimsSet = resolveAndValidateToken(token, access);
        if (claimsSet == null) {
            throw new AuthenticationException("Invalid token");
        }
        return claimsSet.getSubject();
    }

    public Authentication getAuthentication(String token, TokenType access) throws JsonProcessingException, JOSEException, ParseException {
        JwtTokenDto jwtToken = OM.readValue(getSubjectPayload(token, access), JwtTokenDto.class);
        CustomAuthToken customAuthToken = new CustomAuthToken(jwtToken.getUserBaseInfo().getEmail(), null, Collections.emptyList());
        customAuthToken.setDetails(jwtToken); // set jwtToken payload as user details ...
        return customAuthToken;
    }


    public JwtTokenDto createJwtTokeDtoFromModel(UserProfileDetailsDto userProfile, int expiredIn) {
        return createTokenDTOFromUserBaseInfo(mapToUserBaseInfo(userProfile), TokenType.ACCESS, expiredIn);
    }

    public UserBaseInfo mapToUserBaseInfo(UserProfileDetailsDto userProfile) {
        return UserBaseInfo.builder()
                .id(userProfile.getUserId())
                .email(userProfile.getEmail())
                .name(userProfile.getName())
                .orgId(userProfile.getOrgId().toString())
                .tenantId(userProfile.getTenantId())
                .roleIds(userProfile.getRoleIds())
                .isActive(userProfile.getIsActive())
                .build();
    }

    /**
     * Creates a JwtTokenDto with dynamic expiration time.
     *
     * @param userBaseInfo user information to embed
     * @param expiryMinutes token validity in minutes
     * @return JwtTokenDto
     */
    public JwtTokenDto createTokenDTOFromUserBaseInfo(
            UserBaseInfo userBaseInfo,
            TokenType tokenType,
            long expiryMinutes
    ) {
        if (userBaseInfo == null) {
            throw new IllegalArgumentException("UserBaseInfo must not be null");
        }
        if (expiryMinutes <= 0) {
            throw new IllegalArgumentException("Expiry minutes must be greater than zero");
        }

        long now = System.currentTimeMillis();
        long expiresAt = now + Duration.ofMinutes(expiryMinutes).toMillis();

        return JwtTokenDto.builder()
                .jwtId(UUID.randomUUID())
                .tokenType(tokenType)
                .userBaseInfo(userBaseInfo)
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();
    }
}
