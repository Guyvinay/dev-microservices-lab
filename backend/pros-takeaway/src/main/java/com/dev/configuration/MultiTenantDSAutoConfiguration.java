package com.dev.configuration;


import com.dev.service.impl.SchemaMultiTenantConnectionProvider;
import com.dev.service.impl.TenantIdentifierResolver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@Data
@Slf4j
public class MultiTenantDSAutoConfiguration {
    @Autowired
    private DataSource ds;


    @Bean
    public SchemaMultiTenantConnectionProvider connectionProvider() {
        log.info("creating bean of: SchemaMultiTenantConnectionProvider.claas");
        return new SchemaMultiTenantConnectionProvider(ds);
    }

    @Bean
    public TenantIdentifierResolver tenantIdentifierResolver() {
        log.info("creating bean of: TenantIdentifierResolver.claas");
        return new TenantIdentifierResolver();
    }
}
