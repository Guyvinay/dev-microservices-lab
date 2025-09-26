package com.dev.security.details;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails extends User {

    private final String orgId;
    private final String tenantId;

    public CustomUserDetails(String username, String password, String orgId, String tenantId, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.orgId = orgId;
        this.tenantId = tenantId;
    }

    public CustomUserDetails(String username, String password, String orgId, String tenantId, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.orgId = orgId;
        this.tenantId = tenantId;
    }

}
