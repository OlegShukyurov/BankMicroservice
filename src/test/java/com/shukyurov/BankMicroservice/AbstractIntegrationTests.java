package com.shukyurov.BankMicroservice;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractIntegrationTests {

    private static final int CASSANDRA_PORT = 9042;

    private static final PostgreSQLContainer postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:13")
            .withReuse(true);

    private static final CassandraContainer cassandraContainer = (CassandraContainer) new CassandraContainer("cassandra:4.1")
            .withInitScript("init-cassandra-test-container.cql")
            .withReuse(true);

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
        cassandraContainer.start();
    }

    @DynamicPropertySource
    public static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.postgres.jdbcUrl", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.postgres.password", postgresContainer::getPassword);
        registry.add("spring.datasource.postgres.username", postgresContainer::getUsername);

        registry.add("spring.data.cassandra.contact-points", cassandraContainer::getContainerIpAddress);
        registry.add("spring.data.cassandra.port", () -> cassandraContainer.getMappedPort(CASSANDRA_PORT));
    }

}
