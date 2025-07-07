package com.dev.security;

import com.dev.oauth2.handler.CustomAuthenticationFailureHandler;
import com.dev.oauth2.handler.OAuth2LoginSuccessHandler;
import com.dev.oauth2.service.CustomOAuth2UserService;
import com.dev.saml.handler.SamlLoginSuccessHandler;
import com.dev.security.details.CustomUserDetailsService;
import com.dev.security.filter.JWTAuthenticationFilter;
import com.dev.security.filter.JWTAuthorizationFilter;
import com.dev.security.filter.RequestLoggingFilter;
import com.dev.security.provider.CustomAccessTokenEndpointHandler;
import com.dev.security.provider.CustomAuthenticationEntryPoint;
import com.dev.security.provider.CustomAuthenticationProvider;
import com.dev.security.provider.CustomBcryptEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.opensaml.security.x509.X509Credential;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.OpenSaml4AuthenticationProvider;
import org.springframework.security.saml2.provider.service.authentication.Saml2AuthenticatedPrincipal;
import org.springframework.security.saml2.provider.service.authentication.Saml2Authentication;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.Saml2MessageBinding;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
//@EnableWebSecurity(debug = true)
@EnableWebSecurity
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
    private final CustomAccessTokenEndpointHandler customAccessTokenEndpointHandler;

    private final SamlLoginSuccessHandler samlLoginSuccessHandler;


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
            CustomAuthenticationEntryPoint customAuthenticationEntryPoint,
            CustomAccessTokenEndpointHandler customAccessTokenEndpointHandler,
            SamlLoginSuccessHandler samlLoginSuccessHandler
            ) {
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
        this.customAccessTokenEndpointHandler = customAccessTokenEndpointHandler;
        this.samlLoginSuccessHandler = samlLoginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        OpenSaml4AuthenticationProvider authenticationProvider = new OpenSaml4AuthenticationProvider();
        authenticationProvider.setResponseAuthenticationConverter(groupsConverter());


        httpSecurity.sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .cors(cors -> cors
                        .configurationSource(corsConfiguration::corsConfiguration)
                )
                .authorizeHttpRequests(auth -> { auth
                        .requestMatchers("/swagger-ui*/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/oauth2/authorize/github", "/oauth2/authorize/google", "/login/saml2/sso/okta").permitAll()
//                        .requestMatchers("/api/v1.0/users*/**").permitAll()
                        .requestMatchers("api/v1.0/organization/setup-org").permitAll()

//                        .requestMatchers("/actuator/prometheus").permitAll()
//                        .requestMatchers("/api/auth/login").permitAll() // Allow login API
                        .requestMatchers("/graphiql*/**").permitAll()
                        .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
//                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Process JWT after username/password authentication
//                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
                .oauth2Login(oauth2 -> oauth2 // Enables OAuth2 login
                        .tokenEndpoint(token-> token
                                .accessTokenResponseClient(customAccessTokenEndpointHandler)
                        )
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/oauth2/authorize") // http://localhost:8000/oauth2/authorize/github
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
                // SAML2 Login Configuration
                .saml2Login(saml -> saml
                        .loginProcessingUrl("/login/saml2/sso/okta") // Matches the ACS URL set in Okta
                        .authenticationManager(new ProviderManager(authenticationProvider))
                .successHandler(samlLoginSuccessHandler)     // Custom handler after login success
                        .failureHandler(customAuthenticationFailureHandler) // Handles login errors
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

    private Converter<OpenSaml4AuthenticationProvider.ResponseToken, ? extends AbstractAuthenticationToken> groupsConverter() {
        Converter<OpenSaml4AuthenticationProvider.ResponseToken, Saml2Authentication> delegate =
                OpenSaml4AuthenticationProvider.createDefaultResponseAuthenticationConverter();

        return responseToken -> {
            Saml2Authentication authentication = delegate.convert(responseToken);
            Saml2AuthenticatedPrincipal principal = (Saml2AuthenticatedPrincipal) authentication.getPrincipal();
            List<String> groups = principal.getAttribute("groups");
            Set<GrantedAuthority> authorities = new HashSet<>();
            if (groups != null) {
                groups.stream().map(SimpleGrantedAuthority::new).forEach(authorities::add);
            } else {
                authorities.addAll(authentication.getAuthorities());
            }
            return new Saml2Authentication(principal, authentication.getSaml2Response(), authorities);
        };
    }

    @Bean
    public RelyingPartyRegistrationRepository relyingPartyRegistrationRepository() {
        RelyingPartyRegistration registration = RelyingPartyRegistration
//                .withRegistrationId("my-idp") // MUST match your ACS URL
                .withRegistrationId("okta") // ✅ Must match the loginProcessingUrl

                .assertingPartyDetails(party -> party
                        .entityId("http://www.okta.com/exkpgl3e67Uc7IuhR5d7")
                        .singleSignOnServiceLocation("https://dev-48844425.okta.com/app/dev-48844425_devauth_1/exkpgl3e67Uc7IuhR5d7/sso/saml")
                        .singleSignOnServiceBinding(Saml2MessageBinding.REDIRECT) // or POST, depending on IdP
                        .wantAuthnRequestsSigned(false)
                        .verificationX509Credentials(creds -> {
                            // You must provide the IdP's certificate here
                            creds.add(Saml2X509Credential.verification(idpCertificate()));
                        })
                )
                .assertionConsumerServiceLocation("http://localhost:8080/login/saml2/sso/okta") // ACS URL
                .entityId("http://www.okta.com/exkpgl3e67Uc7IuhR5d7") // This should match what your IdP expects
                .signingX509Credentials(creds -> {
                    // Optional: use your app's signing credentials if needed
                })
                .build();

        return new InMemoryRelyingPartyRegistrationRepository(registration);
    }

    // Replace this with actual certificate loading
    private X509Certificate idpCertificate() {
        try (InputStream is = new ClassPathResource("idp-certificate.pem").getInputStream()) {
            CertificateFactory factory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) factory.generateCertificate(is);
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load IdP certificate", ex);
        }
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
