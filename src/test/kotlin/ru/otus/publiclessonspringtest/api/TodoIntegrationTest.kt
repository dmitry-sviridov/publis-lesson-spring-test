package ru.otus.publiclessonspringtest.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatusCode
import ru.otus.publiclessonspringtest.api.dto.GenericResponse
import ru.otus.publiclessonspringtest.configuration.TestPublicLessonSpringTestApplication
import ru.otus.publiclessonspringtest.entity.TodoItemEntity
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import ru.otus.publiclessonspringtest.repository.TodoItemRepository
import ru.otus.publiclessonspringtest.repository.TodoListRepository
import java.time.OffsetDateTime

@SpringBootTest(
    classes = [TestPublicLessonSpringTestApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class TodoIntegrationTest(@LocalServerPort var applicationPort: Int){

    @Autowired
    private lateinit var testRestTemplate: TestRestTemplate

    @Autowired
    private lateinit var todoListRepository: TodoListRepository

    @Autowired
    private lateinit var todoItemRepository: TodoItemRepository

    @Test
    fun `when try to complete todo item in another list, got error`() {
        val todoList1 = TodoListEntity("test1", OffsetDateTime.now(), mutableListOf())
        val todoList2 = TodoListEntity("test2", OffsetDateTime.now(), mutableListOf())

        todoListRepository.save(todoList1)
        todoListRepository.save(todoList2)

        val todoItemForList1 = TodoItemEntity("test todo item 1", false, todoList1, null)
        todoItemRepository.save(todoItemForList1)

        val response = this.testRestTemplate.exchange(
            "http://localhost:$applicationPort/api/todo/${todoList2.id}/markAsDone/${todoItemForList1.id}",
            HttpMethod.GET,
            null,
            object: ParameterizedTypeReference<GenericResponse<Map<String, String>>>() {},
        )

        assertEquals(HttpStatusCode.valueOf(403), response.statusCode)
        assertFalse(response.body!!.isSuccess)
    }
}
