package com.shukyurov.BankMicroservice;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest
public abstract class AbstractIntegrationTests {

    private static final int CASSANDRA_PORT = 9042;
    private static final String KEYSPACE_NAME = "test";

    private static final PostgreSQLContainer postgresContainer = (PostgreSQLContainer) new PostgreSQLContainer("postgres:latest")
            .withReuse(true);

    private static final CassandraContainer cassandraContainer = (CassandraContainer) new CassandraContainer("cassandra:latest")
            .withInitScript("init-cassandra-test-container.cql")
            .withReuse(true);

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
        cassandraContainer.start();
        System.setProperty("spring.datasource.cassandra.jdbcUrl", "jdbc:cassandra://127.0.0.1:" + cassandraContainer.getContactPoint().getPort() + ";DefaultKeyspace=test;AuthMech=1");
        System.setProperty("spring.data.cassandra.keyspace-name", KEYSPACE_NAME);
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
