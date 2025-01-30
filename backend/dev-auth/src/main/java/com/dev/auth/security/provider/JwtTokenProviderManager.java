package com.dev.auth.security.provider;

import com.dev.auth.security.SecurityConstants;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtTokenProviderManager {

    private JWSSigner reqSigner;

    @Autowired
    private SecurityConstants securityConstants;

    @PostConstruct
    protected void postConstruct() throws JOSEException {
        String secretKey = securityConstants.getSigningSecretKey();
        reqSigner = new MACSigner(secretKey.getBytes());
    }

    public String createJwtToken(String payload, int expInMinutes) throws JOSEException {
        return createJwtToken(payload, expInMinutes, reqSigner);
    }

    public String createJwtToken(String payload, int expiryTimeMinutes, JWSSigner jwsSigner) throws JOSEException {

        ZonedDateTime zonedDateTime = LocalDateTime.now().atZone(ZoneOffset.UTC);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(payload)
                .issuer("auth")
                .expirationTime(Date.from(zonedDateTime.plusMinutes(expiryTimeMinutes).toInstant()))
                .issueTime(Date.from(zonedDateTime.toInstant()))
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

    public Map<String, String> tokenPainBasedOnUserProfile(String orgId, String username, String email, String tenantId) {
        Map<String, String> tokenMap = new HashMap<>();
        return tokenMap;
    }

}
