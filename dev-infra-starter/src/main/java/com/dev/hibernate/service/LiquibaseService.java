package com.dev.hibernate.service;

import jakarta.websocket.DeploymentException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component("liquibase-service")
@DependsOn("spring-liquibase")
@Slf4j
public class LiquibaseService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SpringLiquibase liquibase;

    @Value("${liquibase.master.file.path}")
    private String changelog;

    @Value("${liquibase.ignore.schema:pg_catalog,information_schema}")
    private String excludeSchema;


    private static final String DATABASECHANGELOGLOCK ="databasechangeloglock";

    public List<String> getExcludeSchema(){
        return StringUtils.isNotBlank(excludeSchema) ? Arrays.asList(excludeSchema.split(","))
                : Arrays.asList("pg_catalog","information_schema");
    }

    @PostConstruct
    public void initialize() throws SQLException, DeploymentException {
        List<String> ignoreSchemas = new ArrayList<>() ;
        try(Connection connection = dataSource.getConnection()) {
            String schema = "";
            try (ResultSet dataSources = connection.getMetaData().getSchemas()){
                while (dataSources.next()) {
                    schema = dataSources.getString("TABLE_SCHEM");
                    schema = schema.toLowerCase();
                    ignoreSchemas = getExcludeSchema();
                    // Pls check with multiple database on default schemas
                    if (!ignoreSchemas.contains(schema)) {
                        log.info("executing liquibase for tenant: {}", schema);
                        updateLock(schema, connection);
                        liquibase.setDataSource(dataSource);
                        liquibase.setDefaultSchema(schema);
                        liquibase.setChangeLog(changelog);
                        liquibase.setShouldRun(true);
                        liquibase.afterPropertiesSet();
                    }
                }
            } catch (Exception ex) {
                if (!ignoreSchemas.contains(schema)) {
                    updateLock(schema, connection);
                }
                log.error("Exception while deployment : {}", ex);
                connection.close();
                throw new DeploymentException("Exception in deployment");
            }
        }

    }

    private void updateLock(String schema, Connection connection) throws SQLException {
        try (ResultSet tablesRS = connection.getMetaData().getColumns(null, schema, DATABASECHANGELOGLOCK, null)){
            if (tablesRS != null && tablesRS.next()) {
                try(Statement statement = connection.createStatement()) {
                    statement.executeUpdate("update \"" + schema + "\".databasechangeloglock set locked='false'");

                }
            }
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }


}
