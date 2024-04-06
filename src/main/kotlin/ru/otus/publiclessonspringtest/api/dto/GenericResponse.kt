package ru.otus.publiclessonspringtest.api.dto

data class GenericResponse<T>(
    val isSuccess: Boolean = true,
    val data: T
)
