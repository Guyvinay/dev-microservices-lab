package com.dev.multiTenant;

/*
import com.dev.multiTenant.impl.SchemaBasedMultiTenantConnectionProvider;
import com.dev.multiTenant.impl.TenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MultiTenantConfig {


    @Autowired
    private DataSource ds;

    @Bean
    public SchemaBasedMultiTenantConnectionProvider connectionProvider() {
        return new SchemaBasedMultiTenantConnectionProvider(ds);
    }

    @Bean
    public TenantIdentifierResolver tenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }

}
*/
