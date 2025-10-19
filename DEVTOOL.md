### Using DevTools with Testcontainers at Development Time
During development, you can use Spring Boot DevTools to reload the code changes without having to completely restart the application.
You can also configure your containers to reuse the existing containers by adding `@RestartScope`.

**Zreo step**
- intellij idea -> settings -> build, execution, deployment -> compiler -> check "Build project automatically"
- intellij idea -> settings -> advanced settings -> check "Allow auto-make to start even if developed application is currently running"

First, Add `spring-boot-devtools` dependency.

**Gradle**

```groovy
testImplementation 'org.springframework.boot:spring-boot-devtools'
```

**Maven**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
```

Next, add `@RestartScope` annotation on container bean definition as follows:

```java
@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfig {

    @Bean
    @ServiceConnection
    @RestartScope
    PostgreSQLContainer<?> postgreSQLContainer(){
        return new PostgreSQLContainer<>("postgres:15-alpine");
    }

}
```

Now when devtools reloads your application, the same containers will be reused instead of re-creating them.

run 
```java
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication
                .from(Application::main)
                .with(ContainersConfig.class)
                //with user test's config, means will create db in the container
                .run(args);
    }
}
```
then you can see the container is reused when you change the code and save it.
not need to recreate the container.