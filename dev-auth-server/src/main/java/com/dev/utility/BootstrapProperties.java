package com.dev.utility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "app.bootstrap")
public class BootstrapProperties {

    /**
     * Master switch for bootstrap execution.
     */
    private boolean enabled = false;

    @Valid
    @NotNull
    private SystemOrg systemOrg;

    @Valid
    @NotNull
    private PublicTenant publicTenant;

    @Valid
    @NotNull
    private Admin admin;

    // ---------- Nested Models ----------

    @Getter
    @Setter
    public static class SystemOrg {

        @NotBlank
        private String name;

        @NotBlank
        private String email;

        @NotNull
        private Long contact;
    }

    @Getter
    @Setter
    public static class PublicTenant {

        @NotBlank
        private String id;

        @NotBlank
        private String name;
    }

    @Getter
    @Setter
    public static class Admin {

        @NotBlank
        private String email;

        @NotBlank
        private String password;

        @NotBlank
        private String name;
    }
}