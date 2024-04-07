package ru.otus.publiclessonspringtest.service

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import ru.otus.publiclessonspringtest.api.exceptions.NotFoundException
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import ru.otus.publiclessonspringtest.repository.TodoItemRepository
import ru.otus.publiclessonspringtest.repository.TodoListRepository
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class TodoServiceTest {

    @Mock
    private lateinit var todoListRepository: TodoListRepository

    @Mock
    private lateinit var todoItemRepository: TodoItemRepository

    @InjectMocks
    private lateinit var todoService: TodoService


    @Test
    fun `when repository find todolist, it returns`() {
        val todolist = TodoListEntity("test", OffsetDateTime.now(), mutableListOf(), 1)
        Mockito.`when`(todoListRepository.findById(any()))
            .thenReturn(Optional.of(todolist))

        Assertions.assertEquals(
            todolist,
            this.todoService.getTodoListById(1)
        )
    }

    @Test
    fun `when repository not find todolist, exception throwm`() {
        Mockito.`when`(todoListRepository.findById(any()))
            .thenReturn(Optional.empty())

        Assertions.assertThrows(NotFoundException::class.java) {
            this.todoService.getTodoListById(1)
        }
    }

}