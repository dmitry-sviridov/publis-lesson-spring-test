package ru.otus.publiclessonspringtest.entity

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.time.ZoneId


@Entity
@Table(name = "t_todo")
data class TodoListEntity(
    @Column(nullable = false)
    val title: String,

    @Column(nullable = false)
    val createdAt: OffsetDateTime = OffsetDateTime.now(ZoneId.of("Europe/Moscow")),

    @OneToMany(mappedBy = "todoList", cascade = [CascadeType.ALL])
    val listItems: MutableList<TodoItemEntity> = mutableListOf(),

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)
