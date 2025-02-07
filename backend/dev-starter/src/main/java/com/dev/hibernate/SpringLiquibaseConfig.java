package com.dev.hibernate;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SpringLiquibaseConfig {
    @Autowired
    private DataSource dataSource;

    @Value("${liquibase.master.file.path}")
    private String changeLogFile;

    @Bean(name = "spring-liquibase")
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(changeLogFile);
        liquibase.setShouldRun(false);
        return liquibase;
    }
}
