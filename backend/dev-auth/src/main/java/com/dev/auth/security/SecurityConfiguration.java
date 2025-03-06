package com.dev.auth.security;

import com.dev.auth.oauth2.handler.OAuth2LoginSuccessHandler;
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

    private final CustomBcryptEncoder customBcryptEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTAuthenticationFilter jwtAuthenticationFilter;
    private final CustomCorsConfiguration corsConfiguration;
    private final RequestLoggingFilter requestLoggingFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfiguration(CustomOAuth2UserService customOAuth2UserService, CustomBcryptEncoder customBcryptEncoder, CustomAuthenticationProvider customAuthenticationProvider, CustomUserDetailsService customUserDetailsService, JWTAuthenticationFilter jwtAuthenticationFilter, CustomCorsConfiguration corsConfiguration, RequestLoggingFilter requestLoggingFilter, OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customBcryptEncoder = customBcryptEncoder;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.corsConfiguration = corsConfiguration;
        this.requestLoggingFilter = requestLoggingFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
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
                            .requestMatchers("/graphiql*/**").permitAll()
                            .anyRequest()
                            .authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
                .addFilterAfter(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .httpBasic(AbstractHttpConfigurer::disable)
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authz ->
                                authz.baseUri("/oauth2/authorize") // Custom login URL
                        ) //http://localhost:8080/dev-auth/oauth2/authorize/github
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(
                                oAuth2LoginSuccessHandler
                        )
                ) // Enables OAuth2 login
//                .oauth2Login(Customizer.withDefaults())
//                .formLogin(AbstractHttpConfigurer::disable)
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults());

                .formLogin(AbstractHttpConfigurer::disable) // Disable default form login
                .httpBasic(AbstractHttpConfigurer::disable); // Disable basic auth

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
