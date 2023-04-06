package com.shukyurov.BankMicroservice.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.postgres")
    public DataSource postgresDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.cassandra")
    public DataSource cassandraDataSource() {
       return DataSourceBuilder.create().build();
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

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(properties.getChangeLog());
        liquibase.setDefaultSchema(properties.getDefaultSchema());
        liquibase.setDropFirst(properties.isDropFirst());
        liquibase.setShouldRun(properties.isEnabled());
        liquibase.setClearCheckSums(properties.isClearChecksums());

        return liquibase;
    }

//    @Bean
//    @ConfigurationProperties(prefix = "spring.datasource.cassandra")
//    public DataSource cassandraDataSource() {
//        return DataSourceBuilder.create().build();
//        try {
//            Class.forName("com.simba.cassandra.jdbc42.Driver");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        com.simba.cassandra.jdbc42.DataSource cassandraDataSource = new com.simba.cassandra.jdbc42.DataSource();
//
//        cassandraDataSource.setURL("jdbc:cassandra://127.0.0.1:9042/my_keyspace;DefaultKeyspace=my_keyspace");
//
//        return cassandraDataSource;
//    }

//    @Bean
//    public SpringLiquibase postgresLiquibase(@Qualifier("postgresDataSource") DataSource postgresDataSource,
//                                             @Qualifier("postgresLiquibaseProperties") LiquibaseProperties liquibaseProperties) {
//
//        SpringLiquibase liquibase = new SpringLiquibase();
//
//        liquibase.setDataSource(postgresDataSource);
//        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
//        liquibase.setShouldRun(liquibaseProperties.isEnabled());
//        liquibase.setClearCheckSums(liquibaseProperties.isClearChecksums());
//        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
//
//        return liquibase;
//    }
//
//    @Bean
//    public SpringLiquibase cassandraLiquibase(@Qualifier("cassandraDataSource") DataSource cassandraDataSource,
//                                              @Qualifier("cassandraLiquibaseProperties") LiquibaseProperties liquibaseProperties) {
//
//        SpringLiquibase liquibase = new SpringLiquibase();
//
//        liquibase.setDataSource(cassandraDataSource);
//        liquibase.setDefaultSchema("my_keyspace");
//        liquibase.setChangeLog(liquibaseProperties.getChangeLog());
//        liquibase.setDropFirst(liquibaseProperties.isDropFirst());
//        liquibase.setShouldRun(liquibaseProperties.isEnabled());
//        liquibase.setClearCheckSums(liquibaseProperties.isClearChecksums());
//
//        return liquibase;
//    }
}
