package com.tomspencerlondon;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
class ContainerTest {

    @Container
    private GenericContainer postgres = new GenericContainer<>(DockerImageName.parse("postgres"));

    @Container
    private static GenericContainer redis = new GenericContainer(DockerImageName.parse("redis"));

    @Test
    void firstContainerTest() {
        Assertions.assertTrue(postgres.isRunning());
        Assertions.assertTrue(redis.isRunning());
        System.out.println("Postgres Containername: " + postgres.getContainerName());
        System.out.println("Redis Containername: " + redis.getContainerName());
    }

    @Test
    void secondContainerTest() {
        Assertions.assertTrue(postgres.isRunning());
        Assertions.assertTrue(redis.isRunning());
        System.out.println("Postgres Containername: " + postgres.getContainerName());
        System.out.println("Redis Containername: " + redis.getContainerName());
    }

}

