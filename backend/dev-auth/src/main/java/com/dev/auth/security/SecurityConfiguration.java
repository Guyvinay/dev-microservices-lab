package com.dev.auth.security;

import com.dev.auth.security.filter.JWTAuthenticationFilter;
import com.dev.auth.security.provider.CustomAuthenticationProvider;
import com.dev.auth.security.provider.CustomBcryptEncoder;
import com.dev.auth.security.provider.CustomUserDetailsAuthenticationProvider;
import com.dev.auth.security.provider.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.List;

@Configuration
//@EnableWebSecurity(debug = true)
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomBcryptEncoder customBcryptEncoder;

    @Bean
    public CustomUserDetailsAuthenticationProvider userDetailsAuthenticationProvider() {
        return new CustomUserDetailsAuthenticationProvider();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userDetailsService, customBcryptEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests( auth-> {
                    auth.requestMatchers("/login").permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customAuthenticationProvider()));
    }
}
