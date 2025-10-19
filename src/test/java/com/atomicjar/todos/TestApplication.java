package com.atomicjar.todos;

import org.springframework.boot.SpringApplication;
import org.springframework.test.context.ActiveProfiles;

//@ActiveProfiles("test")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(Application::main)
                .with(ContainersConfig.class)
                //with user test's config, means will create db in the container
                .run(args);
    }
}
