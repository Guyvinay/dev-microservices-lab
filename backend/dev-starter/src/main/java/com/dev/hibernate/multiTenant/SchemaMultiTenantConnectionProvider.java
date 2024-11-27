package com.dev.hibernate.multiTenant;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
public class SchemaMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> implements HibernatePropertiesCustomizer {

    private DataSource ds;

    public SchemaMultiTenantConnectionProvider(DataSource source) {
        this.ds = source;
    }

    @Override
    protected DataSource selectAnyDataSource() {
        return this.ds;
    }

    @Override
    protected DataSource selectDataSource(String tenantIdentifier) {
        return this.ds;
    }

    @Override
    public Connection getConnection(String tenantId) throws SQLException {
        Connection connection = super.getConnection(tenantId);

        connection.setSchema(tenantId);

        try {
            String metaData = connection.getSchema();// get the schema if exist in the database
            log.info("schema: {}", metaData);
        } catch (Exception e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    @Override
    public void releaseConnection(String s, Connection connection) throws SQLException {
        connection.setSchema(s);
        connection.close();
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
