# Открытый урок по теме "Тестирование приложений на Spring Framework"

## Описание приложения:
В качестве демонстрационного приложения используется усеченный по функциональности TODO-list.
Доступны следующие эндпоинты:

*Для упрощения демонстрации в браузере все эндпоинты используют глагол GET.*

- Создание нового списка задач:  
  `GET /api/todo/create?title=Название-списка-дел`   
  Параметр title является обязательным  
- Получение информации о списке задач  
   `GET /api/todo/{listId}`
- Создание новой задачи в списке задач  
  `GET /api/todo/{listId}/create?title=Название задачи"`  
   Параметр title является обязательным
- Пометить задачу как выполненную
  `GET /api/todo/{listId}/markAsDone/{itemId}`
 
## Типы тестов в Spring

### Unit тесты

#### Без использования Spring
Используется расширение для Junit @ExtendWith(MockitoExtension::class)  
Мок объекты для создания инстанса тестируемого класса помечаются аннотацией @Mock   
Экземпляр тестируемого класса в тесте помечается аннотацией @InjectMocks

Пример:
```kotlin
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
}
```

Одна из важнейших возможностей Spring Boot — выполнение тестов без необходимости в какой-то определенной инфраструктуре. 
Модуль тестирования Spring Boot включает так называемые срезы (slices), предназначенные для тестирования конкретных частей 
приложения без использования сервера или СУБД. [см. статью](https://habr.com/ru/companies/piter/articles/506872/)
#### Тестирование контроллера:

Используется аннотация @WebMvcTest, принимающая в качестве параметра класс тестируемого контроллера.

Аннотация включает в себя аннотации
- @AutoConfigureMockMvc    

Аннотация используется для автоматической настройки MockMvc в тестах. 
MockMvc представляет собой инструмент от Spring Framework, который позволяет эмулировать HTTP-запросы к вашему 
веб-приложению без реального запуска сервера.
- @ExtendWith(SpringExtension.class)   

Аннотация используется для интеграции Spring Framework с тестами JUnit. 
Когда вы добавляете эту аннотацию к вашему тестовому классу, она позволяет JUnit включить поддержку Spring, 
что обеспечивает выполнение настроек Spring перед запуском тестов и очистку контекста Spring после их завершения.


```
Using this annotation will disable full auto-configuration and instead apply only configuration relevant to MVC tests 
(i.e. @Controller, @ControllerAdvice, @JsonComponent, Converter/GenericConverter, Filter,
WebMvcConfigurer and HandlerMethodArgumentResolver beans
but not @Component, @Service or @Repository beans).
```
Используя аннотацию @MockBean можно подменить необходимые контроллеру компоненты.

Пример:
```kotlin
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
}
```
#### Тестирование сериализации:
Аннотация [@JsonTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/json/JsonTest.html) автоматически настраивает сериализатор, использующийся в приложении и включает в себя аннотацию
@ExtendWith(SpringExtension.class)  

Используется для проверки сериализации в Json, пример:
```kotlin
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
}
```

#### Тестирование репозитория
Аннотация [@DataJpaTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/autoconfigure/orm/jpa/DataJpaTest.html)  
По умолчанию сканирует классы @Entity и настраивает репозитории Spring Data JPA.   
Если в пути к классам доступна встроенная база данных, она также настраивается.  
По умолчанию тесты JPA данных являются транзакционными и откатываются в конце каждого теста. 
Тесты Data JPA также могут внедрять bean-компонент TestEntityManager, который представляет собой альтернативу
стандартному JPA EntityManager, специально разработанному для тестов. 
Так же, существуют аннотации @DataJdbcTest, @JdbcTest для репозиториев, не использующих JPA.

Аннотация принимает параметр bootstrapMode = BootstrapMode.DEFAULT - инициализация репозиториев будет происходить при внедреннии - это дефолтное поведение.
При использовании же параметра bootstrapMode = BootstrapMode.LAZY, Spring регистрирует определение bean-компонента нашего репозитория, 
но не создает его экземпляр сразу. Таким образом, при использовании ленивой опции первое использование запускает ее инициализацию.

Для использования реальной базы данных, нужно добавить аннотацию @AutoConfigureTestDatabase(replace=Replace.NONE)

## Интеграционные тесты
У приложений на Spring Boot есть ApplicationContext, который необходим для интеграционных проверок в тестах.
@SpringBootTest будет заполнять весь контекст приложения для теста. При использовании этой аннотации важно понимать 
атрибут webEnvironment. Без указания этого атрибута такие тесты не будут запускать встроенный контейнер сервлетов 
(например, Tomcat) и вместо этого будут использовать имитацию среды сервлетов. 
Следовательно, ваше приложение не будет доступно через локальный порт.

[@SpringBootTest](https://docs.spring.io/spring-boot/docs/current/api/org/springframework/boot/test/context/SpringBootTest.html), 
так же включает в себя аннотацию ExtendWith(SpringExtension.class).

Для подмены бинов можно использовать класс с тестовой конфигурацией (@TestConfiguration), 

Для отправки http запросов используется бин TestRestTemplate

Пример:
```kotlin
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
```
