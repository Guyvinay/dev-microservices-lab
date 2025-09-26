package com.dev.hibernate.multiTenant;

import com.dev.hibernate.SchemaInitializer;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.engine.jdbc.connections.spi.AbstractDataSourceBasedMultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
public class SchemaMultiTenantConnectionProvider extends AbstractDataSourceBasedMultiTenantConnectionProviderImpl<String> implements HibernatePropertiesCustomizer {

    private final DataSource ds;
    private final SchemaInitializer initializer;

    /**
     *
     * @param source
     * @param initializer
     */
    public SchemaMultiTenantConnectionProvider(DataSource source, SchemaInitializer initializer) {
        this.ds = source;
        this.initializer = initializer;
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
        if (StringUtils.hasText(tenantId)) {
            connection.setSchema(tenantId);
        }
        try {
            String schema = connection.getSchema();// get the schema if exist in the database
            log.info("schema: {}", schema);
            if (schema == null) {
                initializer.initialize(tenantId);
            }
        } catch (Exception e) {
            connection.close();
            throw e;
        }
        return connection;
    }

    @Override
    public void releaseConnection(String s, Connection connection) throws SQLException {
        connection.setSchema("PUBLIC");
        connection.close();
    }

    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put(AvailableSettings.MULTI_TENANT_CONNECTION_PROVIDER, this);
    }
}
