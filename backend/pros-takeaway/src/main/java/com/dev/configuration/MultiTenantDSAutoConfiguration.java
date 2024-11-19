package com.dev.configuration;

import com.dev.service.impl.SchemaMultiTenantConnectionProvider;
import com.dev.service.impl.TenantIdentifierResolver;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//@Configuration
public class MultiTenantDSAutoConfiguration {


    @Autowired
    private DataSource ds;

    @Bean
    public SchemaMultiTenantConnectionProvider connectionProvider() {
        return new SchemaMultiTenantConnectionProvider(ds);
    }

    @Bean
    public TenantIdentifierResolver tenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(
            CurrentTenantIdentifierResolver tenantIdentifierResolver,
            SchemaMultiTenantConnectionProvider connectionProvider) {
        return hibernateProperties -> {
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_IDENTIFIER_RESOLVER, tenantIdentifierResolver);
            hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, connectionProvider);
        };
    }


}
