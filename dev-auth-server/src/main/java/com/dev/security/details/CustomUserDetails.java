package com.dev.security.details;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final UserBaseInfo userBaseInfo;

    public CustomUserDetails(String username, String password, UserBaseInfo userBaseInfo, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.userBaseInfo = userBaseInfo;
    }

    public CustomUserDetails(String username, String password, UserBaseInfo userBaseInfo, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userBaseInfo = userBaseInfo;
    }

}
