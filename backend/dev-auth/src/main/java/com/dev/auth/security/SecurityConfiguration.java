package com.dev.auth.security;

import com.dev.auth.oauth2.handler.CustomAuthenticationFailureHandler;
import com.dev.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.dev.auth.oauth2.service.CustomOAuth2UserService;
import com.dev.auth.security.details.CustomUserDetailsService;
import com.dev.auth.security.filter.JWTAuthenticationFilter;
import com.dev.auth.security.filter.JWTAuthorizationFilter;
import com.dev.auth.security.filter.RequestLoggingFilter;
import com.dev.auth.security.provider.CustomAuthenticationEntryPoint;
import com.dev.auth.security.provider.CustomAuthenticationProvider;
import com.dev.auth.security.provider.CustomBcryptEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
//@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomBcryptEncoder customBcryptEncoder;
    private final CustomAuthenticationProvider customAuthenticationProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final JWTAuthorizationFilter jwtAuthorizationFilter;
    private final CustomCorsConfiguration corsConfiguration;
    private final RequestLoggingFilter requestLoggingFilter;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    public SecurityConfiguration(
            CustomOAuth2UserService customOAuth2UserService,
            CustomBcryptEncoder customBcryptEncoder,
            CustomAuthenticationProvider customAuthenticationProvider,
            CustomUserDetailsService customUserDetailsService,
            JWTAuthorizationFilter jwtAuthorizationFilter,
            CustomCorsConfiguration corsConfiguration,
            RequestLoggingFilter requestLoggingFilter,
            OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
            CustomAuthenticationFailureHandler customAuthenticationFailureHandler,
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customBcryptEncoder = customBcryptEncoder;
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
        this.corsConfiguration = corsConfiguration;
        this.requestLoggingFilter = requestLoggingFilter;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors
                        .configurationSource(corsConfiguration::corsConfiguration)
                )
                .authorizeHttpRequests(auth -> { auth
                        .requestMatchers("/swagger-ui*/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/oauth2/authorize/github", "/oauth2/authorize/google").permitAll()
//                        .requestMatchers("/api/v1.0/users*/**").permitAll()
//                        .requestMatchers("/actuator/prometheus").permitAll()
//                        .requestMatchers("/api/auth/login").permitAll() // Allow login API
                        .requestMatchers("/graphiql*/**").permitAll()
                        .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Process JWT after username/password authentication
                .oauth2Login(oauth2 -> oauth2 // Enables OAuth2 login
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorize")
                        ) // Custom login URL
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/login/oauth2/code/*")
                        ) // Ensures GitHub redirects correctly
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // Custom JWT handler
                        .failureHandler(customAuthenticationFailureHandler)
                ) // http://localhost:8080/oauth2/authorize/github
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
     * ✅ Explicitly defining DaoAuthenticationProvider as a Bean
     */
//    @Bean
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
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(customBcryptEncoder);

        return new ProviderManager(List.of(customAuthenticationProvider, daoAuthenticationProvider));
    }

}
