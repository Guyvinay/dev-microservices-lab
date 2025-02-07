package com.dev;

import com.dev.filter.JWTAuthenticationFilter;
import com.dev.filter.RequestLoggingFilter;
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
@EnableWebSecurity(debug = true)
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
        httpSecurity
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors ->
                        cors.configurationSource(corsConfiguration::corsConfiguration)
                )
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui*/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/v1.0/users*/**").permitAll()
                            .requestMatchers("/api/auth/login").permitAll() // Allow login API
                            .anyRequest()
                            .authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(Customizer.withDefaults())
//                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

}
