package com.atomicjar.todos;

import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    //Now when devtools reloads your application, the same containers will be reused instead of re-creating them
    PostgreSQLContainer<?> postgreSQLContainer(){
        //default acc/pwd = test/
        //psql -U test
        //\c test
        //SELECT * FROM todos LIMIT 10;

        System.out.println("lewis ContainersConfig.postgreSQLContainer");
        return new PostgreSQLContainer<>("postgres:15-alpine");
    }

}
