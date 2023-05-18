package com.tomspencerlondon;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
public class AbstractContainerTest {
    @Container
    protected final GenericContainer postgres = new GenericContainer<>(DockerImageName.parse("postgres"));

    @Container
    protected static GenericContainer redis = new GenericContainer<>(DockerImageName.parse("redis"));
}
