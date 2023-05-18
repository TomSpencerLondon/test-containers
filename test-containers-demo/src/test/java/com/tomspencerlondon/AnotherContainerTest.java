package com.tomspencerlondon;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnotherContainerTest extends AbstractContainerTest {

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