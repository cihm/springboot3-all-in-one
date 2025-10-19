package com.atomicjar.todos;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//Starts the entire Spring Boot application context and enables a random web server port, suitable for integration testing.

@Testcontainers
//This is a JUnit 5 extension annotation provided by Testcontainers, which starts and manages the lifecycle of the marked containers. When used with `@Container`, JUnit will start the container before tests and automatically stop it afterward.
class ApplicationTests {

    @Container
    //Marks `postgres` as a Postgres container managed by Testcontainers. Since it is `static`, the container starts once before all tests and stops after all tests complete.
    @ServiceConnection
    //Provided by Spring Boot 3.1+, this annotation automatically registers the container's JDBC connection information into the Spring Boot environment, eliminating the need for manual data source configuration.
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Test
    void contextLoads() {
        System.out.println("lewis ApplicationTests.contextLoads" );
        //An empty method typically used to test whether the Spring application can start successfully. It serves as the most basic health check.
    }

}
