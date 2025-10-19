package com.atomicjar.todos.repository;

import com.atomicjar.todos.entity.Todo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;

public interface TodoRepository extends ListCrudRepository<Todo, String>, ListPagingAndSortingRepository<Todo, String> {
    @Query("select t from Todo t where t.completed = false")
    List<Todo> getPendingTodos();

    // TodoRepository 保持原樣，使用 Spring Data JPA 提供的 findAll(Sort) 方法
    List<Todo> findAll(org.springframework.data.domain.Sort sort);

}
