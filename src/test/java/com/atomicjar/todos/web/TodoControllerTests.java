package com.atomicjar.todos.web;

import com.atomicjar.todos.entity.Todo;
import com.atomicjar.todos.repository.TodoRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;

//@ActiveProfiles("test") //if comment this, will user application.properties
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class TodoControllerTests {
    @LocalServerPort
    private Integer port;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach
    void setUp() {
        //todoRepository.deleteAll();
        RestAssured.baseURI = "http://localhost:" + port;
        System.out.println("lewis TodoControllerTests:setUp");

    }

    @Test
    void shouldGetAllTodosSortedByOrderAsc() {
        System.out.println("lewis shouldGetAllTodosSortedByOrderAsc:contextLoads");
        List<Todo> todos = List.of(
                new Todo(null, "Todo Item 1", false, 2),
                new Todo(null, "Todo Item 2", false, 4),
                new Todo(null, "Todo Item 3", false, 5),
                new Todo(null, "Todo Item 4", false, 1),
                new Todo(null, "Todo Item 5", false, 3)
        );
        todoRepository.saveAll(todos);
        System.out.println("lewis input:"+todos.toString());

        given()
                .contentType(ContentType.JSON)
                .queryParam("sortBy", "order")
                .queryParam("direction", "desc")
                .when()
                .get("/todos/sorted")
                .then()
                .statusCode(200)
                .body(".", hasSize(5))
                .body("[0].order", is(5))
                .body("[1].order", is(4))
                .body("[2].order", is(3))
                .body("[3].order", is(2))
                .body("[4].order", is(1));
    }

    @Test
    void shouldGetAllTodos() {

        List<Todo> todos = List.of(
                new Todo(null, "Todo Item 1", false, 1),
                new Todo(null, "Todo Item 2", false, 2)
        );
        todoRepository.saveAll(todos);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/todos")
                .then()
                .statusCode(200)
                .body(".", hasSize(2));

        System.out.println("lewis TodoControllerTests:shouldGetAllTodos");

    }

    @Test
    void shouldGetTodoById() {
        Todo todo = todoRepository.save(new Todo(null, "Todo Item 1", false, 1));

        given()
                .contentType(ContentType.JSON)
                .when()
                .get("/todos/{id}", todo.getId())
                .then()
                .statusCode(200)
                .body("title", is("Todo Item 1"))
                .body("completed", is(false))
                .body("order", is(1));

        System.out.println("lewis TodoControllerTests:shouldGetTodoById");

    }

    @Test
    void shouldCreateTodoSuccessfully() {
        given()
                .contentType(ContentType.JSON)
                .body(
                    """
                    {
                        "title": "Todo Item 1",
                        "completed": false,
                        "order": 1
                    }
                    """
                )
                .when()
                .post("/todos")
                .then()
                .statusCode(201)
                .body("title", is("Todo Item 1"))
                .body("completed", is(false))
                .body("order", is(1));

        System.out.println("lewis TodoControllerTests:shouldCreateTodoSuccessfully");

    }

    @Test
    void shouldDeleteTodoById() {
        Todo todo = todoRepository.save(new Todo(null, "Todo Item 1", false, 1));

        assertThat(todoRepository.findById(todo.getId())).isPresent();
        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/todos/{id}", todo.getId())
                .then()
                .statusCode(200);

        assertThat(todoRepository.findById(todo.getId())).isEmpty();

        System.out.println("lewis TodoControllerTests:shouldDeleteTodoById");

    }
}
