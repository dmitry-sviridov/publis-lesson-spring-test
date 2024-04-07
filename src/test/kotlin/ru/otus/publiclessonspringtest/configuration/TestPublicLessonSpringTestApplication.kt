package ru.otus.publiclessonspringtest.configuration

import org.springframework.boot.fromApplication
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.boot.with
import org.springframework.context.annotation.Bean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import ru.otus.publiclessonspringtest.PublicLessonSpringTestApplication

@TestConfiguration(proxyBeanMethods = false)
class TestPublicLessonSpringTestApplication {

	@Bean
	@ServiceConnection
	fun postgresContainer(): PostgreSQLContainer<*> {
		return PostgreSQLContainer(DockerImageName.parse("postgres:15"))
	}

}

fun main(args: Array<String>) {
	fromApplication<PublicLessonSpringTestApplication>().with(TestPublicLessonSpringTestApplication::class).run(*args)
}
