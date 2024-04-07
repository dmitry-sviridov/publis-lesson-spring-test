package ru.otus.publiclessonspringtest.api

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.boot.test.json.JacksonTester
import ru.otus.publiclessonspringtest.entity.TodoItemEntity
import ru.otus.publiclessonspringtest.entity.TodoListEntity
import java.time.OffsetDateTime

@JsonTest
class SerializationTest {

    @Autowired
    lateinit var jacksonTester: JacksonTester<TodoListEntity>

    @Test
    fun serializeEmptyTodoListTest() {
        val date = OffsetDateTime.parse("2021-09-30T15:30:30+03:00")
        val todo = TodoListEntity("test", date, mutableListOf(), 1)
        val json = jacksonTester.write(todo)

        assertThat(json.json).isEqualTo(
            """
            {"title":"test","createdAt":"$date","listItems":[],"id":1}
        """.trimIndent()
        )
    }

    @Test
    fun serializeNotEmptyTodoListTest() {
        val date = OffsetDateTime.parse("2021-09-30T15:30:30+03:00")
        val todo = TodoListEntity("test", date, mutableListOf(), 1)
        val todoItem1 = TodoItemEntity("test1", false, todo, 10)
        val todoItem2 = TodoItemEntity("test2", true, todo, 11)
        todo.listItems.addAll(arrayOf(todoItem1, todoItem2))
        val json = jacksonTester.write(todo)

        assertThat(json.json).isEqualTo(
            """
            {"title":"test","createdAt":"$date","listItems":[{"description":"test1","isDone":false,"id":10},{"description":"test2","isDone":true,"id":11}],"id":1}
        """.trimIndent()
        )
    }

}
