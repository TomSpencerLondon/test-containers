package com.tomspencerlondon;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class ContainerTest extends AbstractContainerTest {

    @Test
    void firstContainerTest() {
        assertThat(postgres.isRunning())
                .isTrue();
    }

    @Test
    void secondContainerTest() {
        assertThat(postgres.isRunning())
                .isTrue();
        assertThat(redis.isRunning())
                .isTrue();

        System.out.println("Posgres Containernam: " + postgres.getContainerName());
        System.out.println("Redis Containernam: " + redis.getContainerName());
    }
}
