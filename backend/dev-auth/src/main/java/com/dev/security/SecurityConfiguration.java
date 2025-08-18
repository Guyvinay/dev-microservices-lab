package com.dev.security;

import com.dev.oauth2.handler.CustomAuthenticationFailureHandler;
import com.dev.oauth2.handler.OAuth2LoginSuccessHandler;
import com.dev.oauth2.service.CustomOAuth2UserService;
import com.dev.saml.handler.Saml2LoginSuccessHandler;
import com.dev.saml.repository.CustomRelyingPartyRegistrationRepository;
import com.dev.saml.resolver.CustomSaml2AuthenticationRequestResolver;
import com.dev.security.details.CustomUserDetailsService;
import com.dev.security.filter.JWTAuthenticationFilter;
import com.dev.security.filter.JWTAuthorizationFilter;
import com.dev.security.filter.RequestLoggingFilter;
import com.dev.oauth2.handler.CustomAccessTokenEndpointHandler;
import com.dev.security.provider.CustomAuthenticationEntryPoint;
import com.dev.security.provider.CustomAuthenticationProvider;
import com.dev.security.provider.CustomBcryptEncoder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.saml2.core.Saml2X509Credential;
import org.springframework.security.saml2.provider.service.authentication.AbstractSaml2AuthenticationRequest;
import org.springframework.security.saml2.provider.service.registration.InMemoryRelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistration;
import org.springframework.security.saml2.provider.service.registration.RelyingPartyRegistrationRepository;
import org.springframework.security.saml2.provider.service.web.DefaultRelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.HttpSessionSaml2AuthenticationRequestRepository;
import org.springframework.security.saml2.provider.service.web.RelyingPartyRegistrationResolver;
import org.springframework.security.saml2.provider.service.web.Saml2AuthenticationRequestRepository;
import org.springframework.security.saml2.provider.service.web.authentication.OpenSaml4AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2AuthenticationRequestResolver;
import org.springframework.security.saml2.provider.service.web.authentication.Saml2WebSsoAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.List;
import java.security.cert.X509Certificate;

import static org.springframework.security.saml2.core.Saml2X509Credential.Saml2X509CredentialType.SIGNING;

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
//    private final CustomRelyingPartyRegistrationRepository relyingPartyRegistrationRepository;
    private final Saml2LoginSuccessHandler saml2LoginSuccessHandler;
//    private final CustomSaml2AuthenticationRequestResolver customSaml2AuthenticationRequestResolver;

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
//            CustomSaml2AuthenticationRequestResolver customSaml2AuthenticationRequestResolver,
//            CustomRelyingPartyRegistrationRepository relyingPartyRegistrationRepository,
            Saml2LoginSuccessHandler saml2LoginSuccessHandler
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
//        this.relyingPartyRegistrationRepository = relyingPartyRegistrationRepository;
        this.saml2LoginSuccessHandler = saml2LoginSuccessHandler;
//        this.customSaml2AuthenticationRequestResolver = customSaml2AuthenticationRequestResolver;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

//        Saml2WebSsoAuthenticationFilter samlFilter = new Saml2WebSsoAuthenticationFilter(
//                relyingPartyRegistrationRepository
//        );

//        samlFilter.setAuthenticationRequestRepository(authenticationRequestRepository());

        httpSecurity.sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // allow session creation for SAML
                )
                .cors(cors -> cors
                        .configurationSource(corsConfiguration::corsConfiguration)
                )
                .authorizeHttpRequests(auth -> {
                    auth
                            .requestMatchers("/swagger-ui*/**", "/v3/api-docs/**").permitAll()
                            .requestMatchers("/oauth2/authorize/github", "/oauth2/authorize/google").permitAll()
                            .requestMatchers("api/v1.0/organization/setup-org").permitAll()
                            .requestMatchers("/graphiql*/**").permitAll()
                            .requestMatchers("/.well-known/**").permitAll()
                            .requestMatchers("/error").permitAll() // Avoid redirect loop
                            .requestMatchers("/login/saml2/**", "/saml2/**").permitAll() // Allow IdP <-> SP communication

                            .anyRequest().authenticated();
                })
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager()), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class) // Process JWT after username/password authentication
                .addFilterBefore(requestLoggingFilter, UsernamePasswordAuthenticationFilter.class)  // Log before authentication
//                .addFilterBefore(samlFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2 // Enables OAuth2 login
                        .tokenEndpoint(token -> token
                                .accessTokenResponseClient(customAccessTokenEndpointHandler)
                        )
                        .authorizationEndpoint(authz -> authz
                                .baseUri("/dev-auth/oauth2/authorize") // http://localhost:8000/oauth2/authorize/github
                        ) // Custom login URL
                        .redirectionEndpoint(redir -> redir
                                .baseUri("/dev-auth/login/oauth2/code/*")
                        )
                        // Ensures GitHub redirects correctly
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2LoginSuccessHandler) // Custom JWT handler
                        .failureHandler(customAuthenticationFailureHandler)
                )
                .saml2Login(saml2 -> saml2
//                                .relyingPartyRegistrationRepository(relyingPartyRegistrationRepository)
//                                .authenticationRequestResolver(authenticationRequestResolver(relyingPartyRegistrationRepository))
                                .successHandler(saml2LoginSuccessHandler)
                                .failureHandler((request, response, exception) -> {
                                    System.out.println("SAML Login failed: " + exception.getMessage());
//                                    response.sendRedirect("/error");
                                })
                        // optionally add successHandler if needed
                )
//                .saml2Login(Customizer.withDefaults())
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


//    @Bean
//    public Saml2AuthenticationRequestResolver authenticationRequestResolver(RelyingPartyRegistrationRepository registrations) {
//        RelyingPartyRegistrationResolver registrationResolver = new DefaultRelyingPartyRegistrationResolver(registrations);
//        OpenSaml4AuthenticationRequestResolver authenticationRequestResolver = new OpenSaml4AuthenticationRequestResolver(registrationResolver);
//        authenticationRequestResolver.setAuthnRequestCustomizer((context) -> context
//                .getAuthnRequest().setForceAuthn(true));
//        return authenticationRequestResolver;
//    }

//    @Bean
//    public RelyingPartyRegistrationRepository samlRegistrationRepository() {
//        String keystorePassword = "changeit";
//        String keyAlias = "saml-key";
//
//        try {
//            KeyStore keyStore = KeyStore.getInstance("JKS");
//            try (InputStream is = new ClassPathResource("saml/saml-keystore.jks").getInputStream()) {
//                keyStore.load(is, keystorePassword.toCharArray());
//            }
//
//            X509Certificate certificate = (X509Certificate) keyStore.getCertificate(keyAlias);
//            RSAPrivateKey privateKey = (RSAPrivateKey) keyStore.getKey(keyAlias, keystorePassword.toCharArray());
//
//            Saml2X509Credential signingCredential = new Saml2X509Credential(privateKey, certificate, SIGNING);
//
//            RelyingPartyRegistration registration = RelyingPartyRegistration
//                    .withRegistrationId("okta")
//                    .entityId("http://localhost:8000/saml2/service-provider-metadata/okta")
//                    .assertionConsumerServiceLocation("http://localhost:8000/login/saml2/sso/okta")
//                    .signingX509Credentials(c -> c.add(signingCredential))
//                    .assertingPartyDetails(party -> party
//                            .entityId("http://www.okta.com/exkpgl3e67Uc7IuhR5d7")
//                            .singleSignOnServiceLocation("https://dev-48844425.okta.com/app/dev-48844425_devauth_1/exkpgl3e67Uc7IuhR5d7/sso/saml")
//                            .wantAuthnRequestsSigned(true)
//                            .verificationX509Credentials(c -> {
//                                // you must also add the IdP's cert here
//                                X509Certificate idpCert = null; // path in classpath
//                                try {
//                                    idpCert = loadCertificate("saml/okta-certificate.cert");
//                                } catch (Exception e) {
//                                    throw new RuntimeException(e);
//                                }
//                                c.add(new Saml2X509Credential(idpCert, Saml2X509Credential.Saml2X509CredentialType.VERIFICATION));
//                            })
//                    )
//                    .build();
//            return new InMemoryRelyingPartyRegistrationRepository(registration);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//
//    }

//    public static X509Certificate loadCertificate(String path) throws Exception {
//        CertificateFactory factory = CertificateFactory.getInstance("X.509");
//        try (InputStream is = new ClassPathResource(path).getInputStream()) {
//            return (X509Certificate) factory.generateCertificate(is);
//        }
//    }

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
     * AuthenticationManager with both Custom and Dao Authentication Providers
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(customBcryptEncoder);

        return new ProviderManager(List.of(customAuthenticationProvider, daoAuthenticationProvider));
    }

//    @Bean
//    public Saml2AuthenticationRequestRepository<AbstractSaml2AuthenticationRequest> authenticationRequestRepository() {
//        return new HttpSessionSaml2AuthenticationRequestRepository(); // default; needs sticky sessions
//    }

}
