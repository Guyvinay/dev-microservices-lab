package com.dev.security.provider;

import com.dev.security.details.ServicePrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class ServiceAuthToken extends AbstractAuthenticationToken {

    private final ServicePrincipal principal;

    public ServiceAuthToken(ServicePrincipal principal) {
        super(
                principal.getScopes().stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList()
        );
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
