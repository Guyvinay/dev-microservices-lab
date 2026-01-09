package com.dev.provider;

import com.dev.details.CustomAuthToken;
import com.dev.details.ServiceAuthToken;
import com.dev.details.ServicePrincipal;
import com.dev.dto.AccessJwtToken;
import com.dev.dto.JwtToken;
import com.dev.dto.ServiceJwtToken;
import com.dev.dto.TokenType;
import com.dev.dto.UserBaseInfo;
import com.dev.exception.AuthenticationException;
import com.dev.exception.JWTTokenException;
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
import java.time.Instant;
import java.util.*;

import static com.dev.SecurityConstants.AUDIENCE;
import static com.dev.SecurityConstants.AUTHORIZATION;
import static com.dev.SecurityConstants.AUTH_TYPE;
import static com.dev.SecurityConstants.CLOCK_SKEW_SECONDS;
import static com.dev.SecurityConstants.ISSUER;
import static com.dev.SecurityConstants.MAX_TOKEN_SIZE;
import static com.dev.SecurityConstants.SIGNING_SECRET_KEY;

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
            JwtToken jwtToken
    ) throws JOSEException, JsonProcessingException {

        validateCreateJwtToken(jwtToken);

        Instant issuedAt = Instant.ofEpochMilli(jwtToken.getCreatedAt());
        Instant expiresAt = Instant.ofEpochMilli(jwtToken.getExpiresAt());

        JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                .jwtID(jwtToken.getJwtId().toString())
                .subject(OM.writeValueAsString(jwtToken))
                .issuer(ISSUER)
                .audience(AUDIENCE)
                .issueTime(Date.from(issuedAt))
                .notBeforeTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .claim(AUTH_TYPE, jwtToken.getTokenType().name())
                ;

        enrichClaims(claimsBuilder, jwtToken);

        SignedJWT signedJWT = new SignedJWT(buildJwsHeader(), claimsBuilder.build());
        signedJWT.sign(reqSigner);

        return signedJWT.serialize();
    }

    private void enrichClaims(JWTClaimsSet.Builder builder, JwtToken payload) {

        if (payload instanceof AccessJwtToken user) {
            builder
                    .claim("userId", user.getUserBaseInfo().getId())
                    .claim("tenantId", user.getUserBaseInfo().getTenantId())
                    .claim("roles", user.getUserBaseInfo().getRoleIds());
        }

        if (payload instanceof ServiceJwtToken service) {
            builder
                    .claim("service", service.getServiceName())
                    .claim("scopes", service.getScopes());
        }
    }

    private void validateServiceClaims(JWTClaimsSet claims) throws ParseException {

        String serviceName = claims.getStringClaim("service");

        if (!StringUtils.hasText(serviceName)) {
            throw new AuthenticationException("Missing service identity");
        }

        List<String> scopes = (List<String>) claims.getClaim("scopes");
        if (CollectionUtils.isEmpty(scopes)) {
            throw new AuthenticationException("Missing service scopes");
        }
    }

    private static void validateCreateJwtToken(JwtToken jwtToken) {
        // Validate required fields
        if (jwtToken.getCreatedAt() <= 0) {
            throw new IllegalArgumentException("createdAt must be set");
        }

        if (jwtToken.getExpiresAt() <= jwtToken.getCreatedAt()) {
            throw new IllegalArgumentException("expiresAt must be greater than createdAt");
        }

        if (jwtToken.getTokenType() == null) {
            throw new IllegalArgumentException("tokenType must be set");
        }

        // ️Ensure JWT ID
        if (jwtToken.getJwtId() == null) {
            jwtToken.setJwtId(UUID.randomUUID());
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

        if (access == TokenType.SERVICE) {
            validateServiceClaims(claims);
        }

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
        AccessJwtToken jwtToken = OM.readValue(getSubjectPayload(token, access), AccessJwtToken.class);
        CustomAuthToken customAuthToken = new CustomAuthToken(jwtToken.getUserBaseInfo().getEmail(), null, Collections.emptyList());
        customAuthToken.setDetails(jwtToken); // set jwtToken payload as user details ...
        return customAuthToken;
    }

    public Authentication getServiceAuthentication(String token) throws ParseException, JOSEException, JsonProcessingException {
        ServiceJwtToken serviceJwtToken = OM.readValue(getSubjectPayload(token, TokenType.SERVICE), ServiceJwtToken.class);

        ServicePrincipal principal = new ServicePrincipal(serviceJwtToken.getServiceName(), serviceJwtToken.getScopes());
        ServiceAuthToken serviceAuthToken = new ServiceAuthToken(principal);
        serviceAuthToken.setDetails(serviceJwtToken);

        return serviceAuthToken;
    }

    public Authentication getAuthenticatedServiceToken(String tenantId) throws ParseException, JOSEException, JsonProcessingException {
        ServiceJwtToken serviceJwtToken = ServiceJwtToken.builder()
                .userBaseInfo(UserBaseInfo.builder()
                        .tenantId(tenantId)
                        .build()
                )
                .serviceName("service-token")
                .scopes(List.of())
                .build();

        ServicePrincipal principal = new ServicePrincipal(serviceJwtToken.getServiceName(), serviceJwtToken.getScopes());
        ServiceAuthToken serviceAuthToken = new ServiceAuthToken(principal);
        serviceAuthToken.setDetails(serviceJwtToken);

        return serviceAuthToken;
    }

}