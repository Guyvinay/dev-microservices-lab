package com.dev.oauth2.dto;

import com.dev.dto.JwtTokenDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private JwtTokenDto jwtTokenDto;
    private Map<String, Object> attributes;
    private Collection<? extends GrantedAuthority> authorities;

    /**
     * @return 
     */
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * @return 
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    /**
     * Returns the name of the authenticated <code>Principal</code>. Never
     * <code>null</code>.
     *
     * @return the name of the authenticated <code>Principal</code>
     */
    @Override
    public String getName() {
        return (jwtTokenDto.getUserId() != null) ? jwtTokenDto.getUserId().toString() : "anonymous";
    }
}
