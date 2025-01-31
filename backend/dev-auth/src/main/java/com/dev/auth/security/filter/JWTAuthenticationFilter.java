package com.dev.auth.security.filter;

import com.dev.auth.security.provider.JwtTokenProviderManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    public static final RequestMatcher REQUESTMATCHER = new AntPathRequestMatcher("/signin", "POST");
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private final JwtTokenProviderManager jwtTokenProvider;

    public JWTAuthenticationFilter(JwtTokenProviderManager jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Same contract as for {@code doFilter}, but guaranteed to be
     * just invoked once per request within a single request thread.
     * See {@link #shouldNotFilterAsyncDispatch()} for details.
     * <p>Provides HttpServletRequest and HttpServletResponse arguments instead of the
     * default ServletRequest and ServletResponse ones.
     *
     * @param request
     * @param response
     * @param filterChain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        System.out.println("JWTAuthenticationFilter invoked for: " + request.getRequestURI());

        String token = jwtTokenProvider.resolveToken(request);
        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 1️⃣ Validate Token
//            if (!jwtTokenProvider.validateToken(token)) {
//                System.out.println("Invalid JWT Token");
//                filterChain.doFilter(request, response);
//                return;
//            }

            // 2️⃣ Extract Username & Authorities from Token (NO DB CALL)
//            String username = jwtTokenProvider.getUsernameFromToken(token);
//            List<GrantedAuthority> authorities = jwtTokenProvider.getAuthoritiesFromToken(token);

            // 3️⃣ Create Authentication Object
//            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, authorities);

            // 4️⃣ Set Security Context
//            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
