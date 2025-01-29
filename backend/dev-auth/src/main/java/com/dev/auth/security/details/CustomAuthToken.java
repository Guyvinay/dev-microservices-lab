package com.dev.auth.security.details;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomAuthToken extends UsernamePasswordAuthenticationToken {

    private String orgId;
    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>UsernamePasswordAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     *
     * @param principal
     * @param credentials
     */
    public CustomAuthToken(String orgId, Object principal, Object credentials) {
        super(principal, credentials);
        setAuthenticated(false);
        this.orgId = orgId;
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param principal
     * @param credentials
     * @param authorities
     */
    public CustomAuthToken(String orgId,Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.orgId = orgId;
    }

    public String getOrgId() {
        return orgId;
    }
}
