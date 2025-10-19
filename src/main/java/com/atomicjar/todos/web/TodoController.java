package com.atomicjar.todos.web;

import com.atomicjar.todos.entity.Todo;
import com.atomicjar.todos.metrics.ApiProcessingMetrics;
import com.atomicjar.todos.repository.TodoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoRepository repository;
    private final ApiProcessingMetrics apiProcessingMetrics;

    public TodoController(TodoRepository repository, ApiProcessingMetrics apiProcessingMetrics) {
        this.repository = repository;
        this.apiProcessingMetrics = apiProcessingMetrics;
    }

    @PostMapping
    public ResponseEntity<Todo> save(@Valid @RequestBody Todo todo) {
        System.out.println("TodoController.save by post: " + todo.toString());
        todo.setId(null);

        Todo saved = apiProcessingMetrics.recordApiToDb(() -> repository.save(todo), todo.toString());


        String uriLocation = ServletUriComponentsBuilder.fromCurrentContextPath()
                .toUriString() + "/todos/" + saved.getId();
        System.out.println("lewis TodoController.save by post:" + todo.toString());
        apiProcessingMetrics.recordLastTodoId(saved.getOrder());

        apiProcessingMetrics.recordUpdateEpoch(Instant.now().getEpochSecond());
        apiProcessingMetrics.recordUpdateInfoLength(todo.toString().length());
        apiProcessingMetrics.recordStringHash(todo.getOrder());


        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Location", uriLocation)
                .body(saved);
    }


    // TodoController 新增可自訂排序欄位與方向的 API
    @GetMapping("/sorted")
    public List<Todo> getAllSorted(
            @RequestParam(defaultValue = "order") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.by(sortBy);
        sort = "desc".equalsIgnoreCase(direction) ? sort.descending() : sort.ascending();

        return repository.findAll(sort);
    }

    @GetMapping
    public Iterable<Todo> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

//    @PostMapping
//    public ResponseEntity<Todo> save(@Valid @RequestBody Todo todo) {
//        System.out.println("lewis TodoController.save by post:" + todo.toString());
//
//        todo.setId(null);
//        Todo savedTodo = repository.save(todo);
//        String uriLocation = ServletUriComponentsBuilder.fromCurrentContextPath().toUriString() + "/todos/" + savedTodo.getId();
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .header("Location", uriLocation)
//                .body(savedTodo);
//    }

    @PatchMapping("/{id}")
    public ResponseEntity<Todo> update(@PathVariable String id, @Valid @RequestBody Todo todo) {
        Todo existingTodo = repository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        if (todo.getCompleted() != null) {
            existingTodo.setCompleted(todo.getCompleted());
        }
        if (todo.getOrder() != null) {
            existingTodo.setOrder(todo.getOrder());
        }
        if (todo.getTitle() != null) {
            existingTodo.setTitle(todo.getTitle());
        }
        Todo updatedTodo = repository.save(existingTodo);
        return ResponseEntity.ok(updatedTodo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable String id) {
        Todo todo = repository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        repository.delete(todo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        repository.deleteAll();
        return ResponseEntity.ok().build();
    }
}
