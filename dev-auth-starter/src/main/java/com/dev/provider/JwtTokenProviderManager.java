package com.dev.provider;

import com.dev.SecurityConstants;
import com.dev.details.CustomAuthToken;
import com.dev.dto.JwtTokenDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProviderManager {

    private JWSSigner reqSigner;
    private JWSVerifier jwsVerifier;
    private final String ISSUER = "dev-auth-server";
    private final List<String> AUDIENCE = Arrays.asList("dev-sandbox", "dev-integration", "dev-auth-server");
    private final List<String> AUTHZ = Arrays.asList("ADMIN", "USER", "MANAGER");
    private final String PERMISSION = "permission";
    private final String AUTH_TOKEN_TYPE = "auth";
    private final String AUTH_TYPE = "type";
    private static final int MAX_TOKEN_SIZE = 4096;
    private final ObjectMapper OM = new ObjectMapper();


    @Autowired
    private SecurityConstants securityConstants;

    @PostConstruct
    protected void postConstruct() throws JOSEException {
        String secretKey = securityConstants.getSigningSecretKey();
        reqSigner = new MACSigner(secretKey.getBytes());
        jwsVerifier = new MACVerifier(secretKey.getBytes());
    }


    public String createJwtToken(String payload, int expiryTimeMinutes) throws JOSEException {

        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(payload) // Token issuer user details.
                .issuer(ISSUER)   // issuer service / entity
                .audience(AUDIENCE)// Token is for my app's user
                .expirationTime(Date.from(zonedDateTime.plusMinutes(expiryTimeMinutes).toInstant()))  // Expire in 15 min
                .issueTime(Date.from(zonedDateTime.toInstant())) // Issue time
                .notBeforeTime(Date.from(zonedDateTime.toInstant())) // Valid from now
                .jwtID(String.valueOf(UUID.randomUUID()))   // Unique token ID
                .claim(PERMISSION, AUTHZ) // custom permission claim
                .claim(AUTH_TYPE, AUTH_TOKEN_TYPE)
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), jwtClaimsSet);
        signedJWT.sign(reqSigner);
        return signedJWT.serialize();
    }


    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader(SecurityConstants.AUTHORIZATION);
        if (Objects.nonNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public JWTClaimsSet getJWTClaimsSet(String token) throws ParseException, JOSEException {
        if (token.getBytes(StandardCharsets.UTF_8).length > MAX_TOKEN_SIZE) {
            throw new JWTTokenException("JWT too large");
        }

        SignedJWT signedJWT = SignedJWT.parse(token);

        if (!signedJWT.verify(jwsVerifier)) {
            log.warn("Signature of the token found invalid.");
            throw new JWTTokenException("Signature of the token found invalid.");
        }

        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

        if (!AUTH_TOKEN_TYPE.equals(jwtClaimsSet.getClaim(AUTH_TYPE))) {
            log.error("Invalid token type found. Accept only Authentication token.");
            throw new JWTTokenException("Invalid token type found. Accept only Authentication token.");
        }

        if (null == jwtClaimsSet.getExpirationTime()) {
            log.error("No expiration time on SignedJWT claim.");
            throw new JWTTokenException("No expiration time on SignedJWT claims");
        }

        ZonedDateTime tokenExpirationTime = ZonedDateTime.ofInstant(jwtClaimsSet.getExpirationTime().toInstant(), ZoneOffset.UTC);
        ZonedDateTime currentDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);

        if (tokenExpirationTime.isBefore(currentDateTime)) {
            log.warn("Jwt token expired to date: {}", currentDateTime);
            throw new AuthenticationException("Jwt token expired to date: " + currentDateTime);
        }

        // issuer
        if (!ISSUER.equals(jwtClaimsSet.getIssuer())) {
            log.warn("Invalid token issuer found: {}", ISSUER);
            throw new AuthenticationException("Invalid token issuer found: {}" + ISSUER);
        }

        // Validate audience
        List<String> tokenAudiences = jwtClaimsSet.getAudience();
        if (Collections.disjoint(tokenAudiences, AUDIENCE)) {
            throw new AuthenticationException("Invalid audience");
        }

        return jwtClaimsSet;
    }

    /**
     * Extracts user details from a valid JWT.
     */
    public String getSubjectPayload(String token) throws JOSEException, ParseException {
        JWTClaimsSet claimsSet = getJWTClaimsSet(token);
        if (claimsSet == null) {
            throw new AuthenticationException("Invalid token");
        }
        return claimsSet.getSubject();
    }

    public Authentication getAuthentication(String token) throws JsonProcessingException, JOSEException, ParseException {
        JwtTokenDto jwtToken = OM.readValue(getSubjectPayload(token), JwtTokenDto.class);
        List<SimpleGrantedAuthority> authorities = jwtToken.getUserBaseInfo().getRoleIds().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        CustomAuthToken customAuthToken = new CustomAuthToken(jwtToken.getUserBaseInfo().getEmail(), null, authorities);
        customAuthToken.setDetails(jwtToken); // set jwtToken payload as user details ...
        return customAuthToken;
    }
}
