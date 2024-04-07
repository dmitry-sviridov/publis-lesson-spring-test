package ru.otus.publiclessonspringtest.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.web.bind.annotation.*
import ru.otus.publiclessonspringtest.api.dto.GenericResponse
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import ru.otus.publiclessonspringtest.service.TodoService

@RestController
@RequestMapping("/api/todo")
class TodoController(private val todoService: TodoService) {

    @GetMapping("/create")
    fun createNewTodoList(@RequestParam title: String): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.saveTodoList(title))
    }

    @GetMapping("/{listId}")
    fun getTodoListById(@PathVariable listId: Long): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.getTodoListById(listId))
    }

    @GetMapping("/{listId}/create")
    fun createTodoItemInTodo(@PathVariable listId: Long, @RequestParam title: String): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.createTodoItem(listId, title))
    }

    @GetMapping("/{listId}/markAsDone/{itemId}")
    fun markTodoItemAsDone(@PathVariable listId: Long, @PathVariable itemId: Long): GenericResponse<TodoListEntity> {
        return GenericResponse(data = todoService.completeTodoItem(itemId = itemId, listId = listId))
    }
}
