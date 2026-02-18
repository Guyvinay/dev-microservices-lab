package com.dev.hibernate.service;


import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class DatasourceService {

    @Autowired
    private DataSource dataSource;

    @Autowired
//    @Qualifier("spring-liquibase")
    private SpringLiquibase springLiquibase;

    @Value("${liquibase.master.file.path}")
    private String changelog;

    @Value("${liquibase.ignore.schema:pg_catalog,information_schema}")
    private String excludeSchema;

    public void executeLiquibase(String tenantId) throws LiquibaseException {
        log.info("Liquibase execution starts for {}", tenantId);
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setDefaultSchema(tenantId);
        springLiquibase.setChangeLog(changelog);
        springLiquibase.setShouldRun(true);
        springLiquibase.afterPropertiesSet();
        log.info("Liquibase execution completed for {}", tenantId);
    }

    public void createSchema(String tenantId) throws SQLException {
        log.info("Schema creation starts for: {}", tenantId);

        try( Connection connection = dataSource.getConnection() ) {
            try(Statement statement = connection.createStatement()) {
                PGobject pgObject = new PGobject();
                pgObject.setType("TEXT");
                pgObject.setValue(tenantId);
                String pgObjectString = pgObject.toString();

                String sanitizeAndValidate = sanitizeAndValidate(pgObjectString);

                if (!sanitizeAndValidate.equals(pgObjectString)) {
                    throw new IllegalArgumentException("Invalid input: " + tenantId);
                }

                String sql = "CREATE SCHEMA IF NOT EXISTS \"" + sanitizeAndValidate + "\"";

                statement.execute(sql);
                log.info("Schema creation complete for: {}", tenantId);
            } catch (SQLException ex ) {
                log.error("Error in executing statement {}", ex.getMessage());
                ex.getStackTrace();
            }
        } catch (SQLException ex) {
            log.error("Error in getting connection {}", ex.getMessage());
            ex.getStackTrace();
        }
    }

    public boolean isSchemaExists(String tenantId) throws SQLException {
        log.info("checking isSchemaExists for {}", tenantId);
        try(Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = connection.getMetaData().getSchemas();
            if(resultSet != null) {
                while(resultSet.next()) {
                    String schema = resultSet.getString("TABLE_SCHEM").toLowerCase();
                    if(tenantId.equals(schema)) {
                        log.info("Schema exists for: {}", tenantId);
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
            log.error("error checking isSchemaExists {}", ex.getMessage());
            ex.getStackTrace();
        }

        log.info("Schema doesn't exists for: {}", tenantId);
        return false;
    }


    private String sanitizeAndValidate(String input) {
        // Regular expression to match only alphanumeric and underscore characters
        String regex = "^[a-zA-Z0-9][a-zA-Z0-9_]*$";

        // Remove any characters that are not alphanumeric or underscores and do not
        // match the above regex
        String sanitizedInput = input.replaceAll("[^\\w]|^(?!" + regex + ").*", "").trim();

        // Check if the sanitized input is a reserved SQL keyword or contains any SQL
        // queries
        String[] reservedKeywords = {"SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "ALTER", "CREATE", "EXECUTE", "TRUNCATE", "UNION"};
        for (String keyword : reservedKeywords) {
            if (sanitizedInput.equalsIgnoreCase(keyword) || sanitizedInput.contains(keyword + " ")) {

                throw new IllegalArgumentException("Invalid input: " + input);
            }
        }
        return sanitizedInput;
    }

    public List<String> getAllTenants() throws SQLException {
        List<String> tenants = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try(ResultSet dataSources = connection.getMetaData().getSchemas()){
                while (dataSources.next()) {
                    String schema = dataSources.getString("TABLE_SCHEM").toLowerCase();
                    tenants.add(schema);
                }
            } catch (Exception e) {
                log.error("Error in getting tenants [{}] ", e);
                connection.close();
            }
            return tenants;
        }
    }

    public List<String> getExcludeSchema(){
        return StringUtils.isNotBlank(excludeSchema) ? Arrays.asList(excludeSchema.split(","))
                : Arrays.asList("pg_catalog","public","information_schema");
    }


}
