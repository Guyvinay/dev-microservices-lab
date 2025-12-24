package com.dev.bootstrap;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "bootstrap")
@Getter @Setter
public class BootstrapConfig {

    private boolean enabled;
    private Organization organization;
    private Tenant tenant;
    private List<Role> roles;
    private AdminUser adminUser;

    @Getter
    @Setter
    public static class Organization {
        private String name;
        private String email;
    }

    @Getter @Setter
    public static class Tenant {
        private String id;
        private String name;
    }

    @Getter @Setter
    public static class Role {
        private String name;
        private boolean admin;
        private String description;
    }

    @Getter @Setter
    public static class AdminUser {
        private String email;
        private String name;
        private String password;
    }
}
