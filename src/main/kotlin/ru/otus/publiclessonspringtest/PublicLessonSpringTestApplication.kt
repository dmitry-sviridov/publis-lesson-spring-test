package ru.otus.publiclessonspringtest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PublicLessonSpringTestApplication

fun main(args: Array<String>) {
	runApplication<PublicLessonSpringTestApplication>(*args)
}
