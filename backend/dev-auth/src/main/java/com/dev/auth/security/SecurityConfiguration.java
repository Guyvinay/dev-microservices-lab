package com.dev.auth.security;

import com.dev.auth.security.filter.JWTAuthenticationFilter;
import com.dev.auth.security.provider.CustomAuthenticationProvider;
import com.dev.auth.security.provider.CustomBcryptEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
//@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomBcryptEncoder customBcryptEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfiguration(CustomBcryptEncoder customBcryptEncoder, CustomAuthenticationProvider customAuthenticationProvider) {
        this.customBcryptEncoder = customBcryptEncoder;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/swagger-ui*/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/api/v1.0/users*/**").permitAll()
                            .requestMatchers("/api/auth/login").permitAll() // Allow login API
                            .anyRequest().authenticated();
                }).csrf(AbstractHttpConfigurer::disable).httpBasic(AbstractHttpConfigurer::disable).formLogin(AbstractHttpConfigurer::disable).addFilterAfter(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customAuthenticationProvider));
    }

}
