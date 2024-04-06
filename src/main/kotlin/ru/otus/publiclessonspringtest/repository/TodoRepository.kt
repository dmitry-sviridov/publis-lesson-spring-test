package ru.otus.publiclessonspringtest.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import ru.otus.publiclessonspringtest.entity.TodoItemEntity
import ru.otus.publiclessonspringtest.entity.TodoListEntity


@Repository
interface TodoListRepository: JpaRepository<TodoListEntity, Long> {

}

@Repository
interface TodoItemRepository: JpaRepository<TodoItemEntity, Long> {

}
