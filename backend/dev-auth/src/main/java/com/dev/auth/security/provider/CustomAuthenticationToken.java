package com.dev.auth.security.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
public class CustomAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final String tenant;

    public CustomAuthenticationToken(Object principal, Object credentials, String tenant) {
        super(principal, credentials);
        this.tenant = tenant;
        super.setAuthenticated(Boolean.FALSE);
    }

    public CustomAuthenticationToken(Object principal, Object credentials, String tenant, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.tenant = tenant;
        super.setAuthenticated(Boolean.TRUE);
    }
}
