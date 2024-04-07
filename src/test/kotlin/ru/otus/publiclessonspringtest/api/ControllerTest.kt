package ru.otus.publiclessonspringtest.api

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.otus.publiclessonspringtest.api.controller.TodoController
import ru.otus.publiclessonspringtest.api.dto.GenericResponse
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import ru.otus.publiclessonspringtest.service.TodoService
import java.time.OffsetDateTime

@WebMvcTest(controllers = [TodoController::class])
class ControllerTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var todoService: TodoService

    @Test
    fun `when new todo creating, response is success`() {
        val date = OffsetDateTime.parse("2021-09-30T15:30:30+03:00")
        val mockTodoList = TodoListEntity(
            title = "test",
            createdAt = date,
            listItems = mutableListOf(),
            id = 100,
        )
        Mockito.`when`(todoService.saveTodoList("test")).thenReturn(mockTodoList)

        val mvcResult = mvc.perform(get("/api/todo/create?title=test"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn()

        verify(todoService, times(1)).saveTodoList("test")

        val json = mvcResult.response.contentAsString
        val response = objectMapper.readValue(json, object : TypeReference<GenericResponse<TodoListEntity>>() {})
        assertTrue(response.isSuccess)
        assertEquals(response.data, mockTodoList)
    }

    @Test
    fun `when new todo creating without title got error`() {
        val mvcResult = mvc.perform(get("/api/todo/create"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andReturn()

        val json = mvcResult.response.contentAsString
        val response = objectMapper.readValue(json, object : TypeReference<GenericResponse<Map<String, String>>>() {})
        assertFalse(response.isSuccess)
        assertEquals(response.data["error"], "Required request parameter 'title' for method parameter type String is not present")
    }
}
