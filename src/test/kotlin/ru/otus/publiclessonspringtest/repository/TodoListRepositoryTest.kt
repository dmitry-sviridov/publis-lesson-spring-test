package ru.otus.publiclessonspringtest.repository

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import ru.otus.publiclessonspringtest.configuration.TestPublicLessonSpringTestApplication
import ru.otus.publiclessonspringtest.entity.TodoItemEntity
import ru.otus.publiclessonspringtest.entity.TodoListEntity

@SpringBootTest(classes = [TestPublicLessonSpringTestApplication::class])
//@ContextConfiguration(classes = [TestPublicLessonSpringTestApplication::class])
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TodoListRepositoryTest {

    @Autowired
    lateinit var todoListRepository: TodoListRepository

    @Autowired
    lateinit var todoItemRepository: TodoItemRepository

    @Test
    @Transactional
    fun `when new todo created it has empty items list`() {
        val todoList = TodoListEntity("test todo")
        todoListRepository.save(todoList)

        val todo = todoListRepository.findById(todoList.id!!)
        with(todo) {
            assertThat(this.isPresent)
            val result = this.get()
            assertThat(result.listItems.isEmpty())
            assertThat(result.title).isEqualTo("test todo")
        }
    }

    @Test
    @Transactional
    fun `when new todo created with todo item, todolist from db contains this item`() {
        val todoList = TodoListEntity("test todo")
        val todoItem = TodoItemEntity("test item", false, todoList)
        todoListRepository.save(todoList)
        todoItemRepository.save(todoItem)

        val todo = todoListRepository.findById(todoList.id!!)
        with(todo) {
            assertThat(this.isPresent)
            val result = this.get()
            assertThat(result.listItems.contains(todoItem))
            assertThat(result.listItems.size == 1)
            assertThat(result.title).isEqualTo("test todo")
        }
    }
}
