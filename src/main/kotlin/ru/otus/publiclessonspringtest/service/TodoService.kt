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

    fun getTodoListById(listId: Long): TodoListEntity {
       return todoListRepository.findById(listId).orElseThrow { NotFoundException() }
    }

    fun createTodoItem(listId: Long, itemDescription: String): TodoListEntity {
        val todo = todoListRepository.findById(listId).orElseThrow { NotFoundException() }
        val todoItem = TodoItemEntity(description = itemDescription, todoList = todo)
        todoItemRepository.save(todoItem)
        return todoItem.todoList
    }

    fun completeTodoItem(itemId: Long, listId: Long): TodoListEntity {
        val todoItem = todoItemRepository.findById(itemId).orElseThrow { NotFoundException() }.apply {
            if (this.todoList.id != listId) {
                throw AccessException()
            }
            isDone = true
            todoItemRepository.save(this)
        }
        return todoItem.todoList
    }

}
