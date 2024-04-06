package ru.otus.publiclessonspringtest.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.otus.publiclessonspringtest.api.dto.GenericResponse
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import ru.otus.publiclessonspringtest.service.TodoService

@RestController
@RequestMapping("/api/todo")
class TodoController(private val todoService: TodoService, val objectMapper: ObjectMapper) {

    @GetMapping("/create")
    fun createNewTodoList(@RequestParam title: String): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.saveTodoList(title))
    }

    @GetMapping("/{id}")
    fun getTodoListById(@PathVariable id: Long): GenericResponse<TodoListEntity?> {
        return GenericResponse(data = todoService.getTodoListById(id))
    }

    @GetMapping("/{id}/create")
    fun createTodoItemInTodo(@PathVariable id: Long, @RequestParam title: String): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.createTodoItem(id, title))
    }

    @GetMapping("/{listId}/markAsDone/{todoItemId}")
    fun markTodoItemAsDone(@PathVariable listId: Long, @PathVariable todoItemId: Long): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.completeTodoItem(itemId = todoItemId, todoListId = listId))
    }
}
