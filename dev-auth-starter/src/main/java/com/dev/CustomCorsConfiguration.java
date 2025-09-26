package com.dev;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.List;

@Component
public class CustomCorsConfiguration {

    /**
     * cfg.setAllowedOrigins(List.of("*"));
     * cfg.setAllowedOriginPatterns(Arrays.asList("https://trusted-domain1.com", "https://trusted-domain2.com"));
     * cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
     * cfg.setAllowCredentials(true);
     * cfg.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "Cache-Control"));
     * cfg.setExposedHeaders(Arrays.asList("Authorization", "X-Response-Time"));
     * cfg.setMaxAge(3600L); // Cache for 1 hour (in seconds)
     *
     * @param request
     * @return
     */
    public CorsConfiguration corsConfiguration(HttpServletRequest request) {
        CorsConfiguration cfg = new CorsConfiguration();

        // Allow all origins (use "*" to allow any origin, but be careful with this in production)
        cfg.setAllowedOrigins(List.of("*"));

        // Allow all origin patterns (to allow cross-origin requests from any domain)
        cfg.setAllowedOriginPatterns(List.of("*"));

        // Allow all HTTP methods (GET, POST, PUT, DELETE, OPTIONS, etc.)
        cfg.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

        // Allow credentials – allows cookies, authorization headers, etc. in CORS requests
        cfg.setAllowCredentials(true);

        // Allow all headers to be sent by clients
        cfg.setAllowedHeaders(List.of("*"));

        // Expose all headers in the response (headers like Authorization, etc.)
        cfg.setExposedHeaders(Arrays.asList("Authorization", "X-Response-Time", "*"));

        // Max age – specify how long the pre-flight request (OPTIONS) can be cached in the browser
        cfg.setMaxAge(3600L); // Cache for 1 hour (in seconds)

        return cfg;
    }

}
