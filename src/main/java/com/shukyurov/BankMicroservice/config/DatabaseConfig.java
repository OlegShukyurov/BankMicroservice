package com.shukyurov.BankMicroservice.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.cassandra.jdbcUrl}")
    private String CASSANDRA_JDBC_URL;

    @Value("${spring.datasource.cassandra.driver-class-name}")
    private String CASSANDRA_DRIVER_CLASS_NAME;

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource cassandraDataSource() {
        try {
            Class.forName(CASSANDRA_DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        com.simba.cassandra.jdbc42.DataSource cassandraDataSource = new com.simba.cassandra.jdbc42.DataSource();

        cassandraDataSource.setURL(CASSANDRA_JDBC_URL);

        return cassandraDataSource;
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgres.liquibase")
    public LiquibaseProperties postgresLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.cassandra.liquibase")
    public LiquibaseProperties cassandraLiquibaseProperties() {
        return new LiquibaseProperties();
    }

    @Bean
    public SpringLiquibase postgresLiquibase() {
        return springLiquibase(postgresDataSource(), postgresLiquibaseProperties());
    }

    @Bean
    public SpringLiquibase cassandraLiquibase() {
        return springLiquibase(cassandraDataSource(), cassandraLiquibaseProperties());
    }

    private static SpringLiquibase springLiquibase(DataSource dataSource, LiquibaseProperties properties) {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setDataSource(dataSource);

        return liquibase;
    }

}
