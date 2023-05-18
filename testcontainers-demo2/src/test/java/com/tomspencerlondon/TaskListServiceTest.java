package com.tomspencerlondon;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.testcontainers.containers.BindMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.Assert.assertNotNull;

@Testcontainers(disabledWithoutDocker = true)
public class TaskListServiceTest {

    @Container
    private GenericContainer postgres = new GenericContainer(DockerImageName.parse("postgres:12.12"))
            .withEnv("POSTGRES_USER", "postgres")
            .withEnv("POSTGRES_PASSWORD", "postgres")
            .withEnv("POSTGRES_DB", "tasklist")
            .withClasspathResourceMapping("database/INIT.sql",
                    "/docker-entrypoint-initdb.d/INIT.sql",
                    BindMode.READ_ONLY)
            .withExposedPorts(5432);

//    @Container
//    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:12.12")
//            .withUsername("postgres")
//            .withPassword("postgres")
//            .withInitScript("database/INIT.sql")
//            .withDatabaseName("tasklist")
//            .withExposedPorts(5432);

    private TaskListService service;

    @BeforeEach
    void setUp() {
        final var dataSource = new PGSimpleDataSource();
        dataSource.setServerName("localhost");
        dataSource.setDatabaseName("tasklist");
        dataSource.setUser("postgres");
        dataSource.setPortNumbers(new int[]{postgres.getMappedPort(5432)});
        dataSource.setPassword("postgres");

        service = new TaskListService(dataSource);
    }

    @Test
    void shouldInsertNewEntries() {
        TaskListEntry entry = service.insert(new TaskListEntry("write tests"));
        assertNotNull(entry);
        assertNotNull(entry.getId());
    }
}
