package com.dev.hibernate;


import com.dev.hibernate.multiTanent.TenantIdentifierResolver;
import com.dev.hibernate.multiTanent.SchemaMultiTenantConnectionProvider;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

//@Configuration
//@Data
//@AutoConfigureBefore(HibernateJpaAutoConfiguration.class)
//@AutoConfigureAfter(DataSourceAutoConfiguration.class)
public class MultiTenantDSAutoConfiguration {
//    @Autowired
    private DataSource ds;


//    @Bean
    public SchemaMultiTenantConnectionProvider connectionProvider() {
        return new SchemaMultiTenantConnectionProvider(ds);
    }

//    @Bean
    public TenantIdentifierResolver tenantIdentifierResolver() {
        return new TenantIdentifierResolver();
    }
}
