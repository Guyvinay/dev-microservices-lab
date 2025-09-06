package com.dev.hibernate;


import com.dev.hibernate.multiTenant.SchemaMultiTenantConnectionProvider;
import com.dev.hibernate.multiTenant.TenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
//@Data
//@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
//@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MultiTenantDSAutoConfiguration {


    @Autowired
    private DataSource ds;

    @Autowired
    @Qualifier("custom-schema-initializer")
    private SchemaInitializer initializer;

    @Bean
    public SchemaMultiTenantConnectionProvider connectionProvider() {
        return new SchemaMultiTenantConnectionProvider(ds, initializer);
    }

    @Bean
    public TenantIdentifierResolver tenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }
}
