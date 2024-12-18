package com.dev.profile.security.provider;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class JwtServiceProvider {


    private final String secretKey = "your-256-bit-secret";
    private final long expirationMillis = 3600000; // 1 hour


    /**
     * JWT Request Signer
     */
    private JWSSigner reqSigner;


    public String generateJwtToken() throws JOSEException {
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()

                .build();

        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

        SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);

        signedJWT.sign(new MACSigner(secretKey.getBytes()));

        return signedJWT.serialize();
    }

}
