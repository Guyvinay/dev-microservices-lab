package com.dev.auth.security;

import com.dev.auth.oauth2.service.CustomOAuth2UserService;
import com.dev.auth.security.details.CustomUserDetailsService;
import com.dev.auth.security.filter.JWTAuthenticationFilter;
import com.dev.auth.security.filter.RequestLoggingFilter;
import com.dev.auth.security.provider.CustomAuthenticationProvider;
import com.dev.auth.security.provider.CustomBcryptEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity(debug = true)
//@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    private final CustomBcryptEncoder customBcryptEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final CustomCorsConfiguration corsConfiguration;
    private final RequestLoggingFilter requestLoggingFilter;

    public SecurityConfiguration(CustomBcryptEncoder customBcryptEncoder, CustomAuthenticationProvider customAuthenticationProvider, CustomUserDetailsService customUserDetailsService, JWTAuthenticationFilter jwtAuthenticationFilter, CustomCorsConfiguration corsConfiguration, RequestLoggingFilter requestLoggingFilter) {
        this.customBcryptEncoder = customBcryptEncoder;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customUserDetailsService = customUserDetailsService;
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
                            .requestMatchers("/", "/login").permitAll()
                            .requestMatchers("/api/auth/login").permitAll() // Allow login API
                            .anyRequest()
                            .authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(Customizer.withDefaults())
                .oauth2Login(oauth2->
                        oauth2.userInfoEndpoint(userInfo->
                                userInfo.userService(customOAuth2UserService)
                        )
                ) // Enables OAuth2 login
//                .oauth2Login(Customizer.withDefaults())
//                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());

        return httpSecurity.build();
    }

    /**
     * ✅ Explicitly defining DaoAuthenticationProvider as a Bean
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(customBcryptEncoder);
        return daoAuthenticationProvider;
    }


    /**
     * ✅ AuthenticationManager with both Custom and Dao Authentication Providers
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(customAuthenticationProvider, daoAuthenticationProvider()));
    }

}
