package com.dev;

import com.dev.filter.JWTAuthenticationFilter;
import com.dev.filter.RequestLoggingFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
//@EnableWebSecurity(debug = true)
public class AuthStaterSecurityConfiguration {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final CustomCorsConfiguration corsConfiguration;
    private final RequestLoggingFilter requestLoggingFilter;

    public AuthStaterSecurityConfiguration(JWTAuthenticationFilter jwtAuthenticationFilter, CustomCorsConfiguration corsConfiguration, RequestLoggingFilter requestLoggingFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfiguration = corsConfiguration;
        this.requestLoggingFilter = requestLoggingFilter;
    }

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
                                    "/graphiql*/**", "/actuator*/**"
                            ).permitAll()
                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Process JWT after username/password authentication
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
                .formLogin(AbstractHttpConfigurer::disable) // Disable default form login
                .httpBasic(AbstractHttpConfigurer::disable); // Disable basic auth

        return httpSecurity.build();
    }

}
