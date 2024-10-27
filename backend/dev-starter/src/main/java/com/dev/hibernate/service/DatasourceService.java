package com.dev.hibernate.service;


import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@Slf4j
public class DatasourceService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SpringLiquibase springLiquibase;

    @Value("${liquibase.master.file.path}")
    private String changelog;

    public void executeLiquidbase(String tenantId) throws LiquibaseException {
        springLiquibase.setDataSource(dataSource);
        springLiquibase.setDefaultSchema(tenantId);
        springLiquibase.setChangeLog(changelog);
        springLiquibase.setShouldRun(true);
        springLiquibase.afterPropertiesSet();
    }

    public boolean isSchemaExists(String tenantId) throws SQLException {
        log.info("checking isSchemaExists for {}", tenantId);
        try(Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = connection.getMetaData().getSchemas();
            if(resultSet != null) {
                while(resultSet.next()) {
                    System.out.println(resultSet.getString("TABLE_SCHEM"));
                }
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return false;
    }


}
