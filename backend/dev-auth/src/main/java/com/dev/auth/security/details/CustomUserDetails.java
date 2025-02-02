package com.dev.auth.security.details;

import com.dev.auth.entity.UserProfileModel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails extends User {

    private final String orgId;

    public CustomUserDetails(String username, String password, String orgId, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.orgId = orgId;
    }

    public CustomUserDetails(String username, String password, String orgId, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.orgId = orgId;
    }

    public CustomUserDetails(UserProfileModel user) {
        super(user.getUsername(), user.getPassword(), user.isActive(), true, true, true,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        this.orgId = "CurrentOrgId";
    }

}
