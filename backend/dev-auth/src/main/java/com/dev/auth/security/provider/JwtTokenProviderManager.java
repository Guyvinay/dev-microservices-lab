package com.dev.auth.security.provider;

import com.dev.auth.exception.JWTTokenExpiredException;
import com.dev.auth.security.SecurityConstants;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.*;

@Slf4j
@Component
public class JwtTokenProviderManager {

    private JWSSigner reqSigner;
    private JWSVerifier jwsVerifier;
    private final String ISSUER = "dev-auth";
    private final List<String> AUDIENCE = Arrays.asList("dev-takeaway", "dev-revised");
    private final List<String> AUTHZ = Arrays.asList("ADMIN", "USER", "MANAGER");
    private final String PERMISSION = "permission";

    @Autowired
    private SecurityConstants securityConstants;

    @PostConstruct
    protected void postConstruct() throws JOSEException {
        String secretKey = securityConstants.getSigningSecretKey();
        reqSigner = new MACSigner(secretKey.getBytes());
        jwsVerifier = new MACVerifier(secretKey.getBytes());
    }

    public String createJwtToken(String payload, int expInMinutes) throws JOSEException {
        return createJwtToken(payload, expInMinutes, reqSigner);
    }

    public String createJwtToken(String payload, int expiryTimeMinutes, JWSSigner jwsSigner) throws JOSEException {

        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(payload) // Token issuer user details.
                .issuer(ISSUER)   // issuer service / entity
                .audience(AUDIENCE)// Token is for my app's user
                .expirationTime(Date.from(zonedDateTime.plusMinutes(expiryTimeMinutes).toInstant()))  // Expire in 15 min
                .issueTime(Date.from(zonedDateTime.toInstant())) // Issue time
                .notBeforeTime(Date.from(zonedDateTime.toInstant())) // Valid from now
                .jwtID(String.valueOf(UUID.randomUUID()))   // Unique token ID
                .claim(PERMISSION, AUTHZ) // custom permission claim
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

        SignedJWT signedJWT = SignedJWT.parse(token);
        if (!signedJWT.verify(jwsVerifier)) {
            log.warn("Signature of the token found invalid.");
            throw new JOSEException("Signature of the token found invalid.");
        }

        JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();

        if (null == jwtClaimsSet.getExpirationTime()) {
            log.error("No expiration time on SignedJWT claim.");
            throw new JOSEException("No expiration time on SignedJWT claims");
        }

        ZonedDateTime tokenExpirationTime = ZonedDateTime.ofInstant(jwtClaimsSet.getExpirationTime().toInstant(), ZoneOffset.UTC);
        ZonedDateTime currentDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);

        if (tokenExpirationTime.isBefore(currentDateTime)) {
            log.warn("Jwt token expired to date: {}", currentDateTime);
            throw new JWTTokenExpiredException("Jwt token expired to date: " + currentDateTime);
        }

        if (ISSUER.equals(jwtClaimsSet.getClaim(ISSUER))) {
            log.warn("Invalid token issuer found: {}", ISSUER);
            throw new JWTTokenExpiredException("Invalid token issuer found: {}" + ISSUER);
        }

        AUDIENCE.forEach((aud)-> {
            if(jwtClaimsSet.getAudience().contains(aud))
                throw new JWTTokenExpiredException("Invalid Audience found...");
        });

        return jwtClaimsSet;
    }

    public Map<String, String> tokenPairBasedOnUserProfile(String orgId, String username, String email, String tenantId) {
        Map<String, String> tokenMap = new HashMap<>();
        return tokenMap;
    }

}
