package com.dev.security;

import com.dev.oauth2.handler.CustomAuthenticationFailureHandler;
import com.dev.oauth2.handler.OAuth2LoginSuccessHandler;
import com.dev.oauth2.service.CustomOAuth2UserService;
import com.dev.security.details.CustomUserDetailsService;
import com.dev.security.filter.JWTAuthenticationFilter;
import com.dev.security.filter.JWTAuthorizationFilter;
import com.dev.security.filter.RequestLoggingFilter;
import com.dev.oauth2.handler.CustomAccessTokenEndpointHandler;
import com.dev.security.provider.CustomAuthenticationEntryPoint;
import com.dev.security.provider.CustomAuthenticationProvider;
import com.dev.security.provider.CustomBcryptEncoder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
//@EnableWebSecurity(debug = true)
//@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    // ====== Core Components ======
    private final CustomUserDetailsService customUserDetailsService;
    private final CustomBcryptEncoder customBcryptEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    // ====== JWT Filters ======
    private final JWTAuthenticationFilter jwtAuthenticationFilter;   // Login filter
    private final JWTAuthorizationFilter jwtAuthorizationFilter;     // Token validator

    // ====== Extra Config ======
    private final CustomCorsConfiguration corsConfiguration;
    private final RequestLoggingFilter requestLoggingFilter;

    // ====== OAuth2 Components ======
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessTokenEndpointHandler customAccessTokenEndpointHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors
                        .configurationSource(corsConfiguration::corsConfiguration)
                )
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers(
                                    "/swagger-ui*/**", "/v3/api-docs*/**",
                                    "/oauth2/authorize/github", "/oauth2/authorize/google",
                                    "/api/v1.0/organization/setup-org",
                                    "/api/auth/request-password-reset",
                                    "/api/auth/validate-reset-password/**",
                                    "/api/auth/reset-password",
                                    "/graphiql*/**", "/actuator*/**"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)

                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLoggingFilter, JWTAuthorizationFilter.class)

                .oauth2Login(oauth2 -> oauth2 // Enables OAuth2 login
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(customAccessTokenEndpointHandler)
                        )
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorize") // http://localhost:8000/dev-auth-server/oauth2/authorize/github
                        ) // Custom login URL
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/login/oauth2/code/*")
                        )
                        // Ensures GitHub redirects correctly
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // Custom JWT handler
                        .failureHandler(customAuthenticationFailureHandler)
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_OK))
                )
                .formLogin(AbstractHttpConfigurer::disable) // Disable default form login
                .httpBasic(AbstractHttpConfigurer::disable); // Disable basic auth

        return httpSecurity.build();
    }

    /**
     * Explicitly defining DaoAuthenticationProvider as a Bean
     */
//    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(customBcryptEncoder);
        return daoAuthenticationProvider;
    }



    // =====================================================================================
    // 1. AuthenticationManager
    // =====================================================================================
    /**
     * AuthenticationManager with both Custom and Dao Authentication Providers
     */
    @Bean
    public AuthenticationManager authenticationManager() {

        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(customUserDetailsService);
        daoProvider.setPasswordEncoder(customBcryptEncoder);

        return new ProviderManager(
                List.of(customAuthenticationProvider, daoProvider)
        );
    }

}
