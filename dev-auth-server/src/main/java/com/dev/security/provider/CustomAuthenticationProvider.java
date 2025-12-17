package com.dev.security.provider;

import com.dev.dto.JwtTokenDto;
import com.dev.security.details.CustomAuthToken;
import com.dev.security.details.CustomUserDetails;
import com.dev.security.details.CustomUserDetailsService;
import com.dev.security.details.UserBaseInfo;
import com.dev.security.utility.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService userDetailsService;
    private final CustomBcryptEncoder customBcryptEncoder;

    public CustomAuthenticationProvider(CustomUserDetailsService userDetailsService, CustomBcryptEncoder customBcryptEncoder) {
        this.userDetailsService = userDetailsService;
        this.customBcryptEncoder = customBcryptEncoder;
    }

    /**
     * Performs authentication with the same contract as
     * {@link AuthenticationManager#authenticate(Authentication)}
     * .
     *
     * @param authentication the authentication request object.
     * @return a fully authenticated object including credentials. May return
     * <code>null</code> if the <code>AuthenticationProvider</code> is unable to support
     * authentication of the passed <code>Authentication</code> object. In such a case,
     * the next <code>AuthenticationProvider</code> that supports the presented
     * <code>Authentication</code> class will be tried.
     * @throws AuthenticationException if authentication fails.
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!(authentication instanceof UsernamePasswordAuthenticationToken))
            throw new BadCredentialsException("Invalid authentication requested.");

        log.info("CustomAuthenticationProvider invoked for: {}", authentication.getPrincipal());

        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        log.info("Authenticating user: {}.", username);

//        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsernameTenantAndOrg(username);

        if (!customBcryptEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        UsernamePasswordAuthenticationToken authenticationToken = new CustomAuthToken(
                userDetails.getPassword(),
                userDetails.getUsername(),
                userDetails.getAuthorities()
        );
        UserBaseInfo userBaseInfo = userDetails.getUserBaseInfo();

        JwtTokenDto jwtTokenDto = SecurityUtils.createToken(userBaseInfo, 200000);
        authenticationToken.setDetails(jwtTokenDto);
        return authenticationToken;
    }

    /**
     * Returns <code>true</code> if this <Code>AuthenticationProvider</code> supports the
     * indicated <Code>Authentication</code> object.
     * <p>
     * Returning <code>true</code> does not guarantee an
     * <code>AuthenticationProvider</code> will be able to authenticate the presented
     * <code>Authentication</code> object. It simply indicates it can support closer
     * evaluation of it. An <code>AuthenticationProvider</code> can still return
     * <code>null</code> from the {@link #authenticate(Authentication)} method to indicate
     * another <code>AuthenticationProvider</code> should be tried.
     * </p>
     * <p>
     * Selection of an <code>AuthenticationProvider</code> capable of performing
     * authentication is conducted at runtime the <code>ProviderManager</code>.
     * </p>
     *
     * @param authentication
     * @return <code>true</code> if the implementation can more closely evaluate the
     * <code>Authentication</code> class presented
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return CustomAuthToken.class.isAssignableFrom(authentication);
    }
}
