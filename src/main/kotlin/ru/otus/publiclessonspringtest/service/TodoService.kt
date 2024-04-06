package ru.otus.publiclessonspringtest.service

import org.springframework.stereotype.Service
import ru.otus.publiclessonspringtest.api.exceptions.AccessException
import ru.otus.publiclessonspringtest.api.exceptions.NotFoundException
import ru.otus.publiclessonspringtest.entity.TodoItemEntity
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import ru.otus.publiclessonspringtest.repository.TodoItemRepository
import ru.otus.publiclessonspringtest.repository.TodoListRepository

@Service
class TodoService(
    private val todoListRepository: TodoListRepository,
    private val todoItemRepository: TodoItemRepository,
){

    fun saveTodoList(title: String): TodoListEntity {
        return todoListRepository.save(TodoListEntity(title = title))
    }

    fun getTodoListById(todoId: Long): TodoListEntity {
       return todoListRepository.findById(todoId).orElseThrow { NotFoundException() }
    }

    fun createTodoItem(todoId: Long, itemDescription: String): TodoListEntity {
        val todo = todoListRepository.findById(todoId).orElseThrow { NotFoundException() }
        val todoItem = TodoItemEntity(description = itemDescription, todoList = todo)
        todoItemRepository.save(todoItem)
        return todoItem.todoList
    }

    fun completeTodoItem(itemId: Long, todoListId: Long): TodoListEntity {
        val todoItem = todoItemRepository.findById(itemId).orElseThrow { NotFoundException() }.apply {
            if (this.todoList.id != todoListId) {
                throw AccessException()
            }
            isDone = true
            todoItemRepository.save(this)
        }
        return todoItem.todoList
    }

}
