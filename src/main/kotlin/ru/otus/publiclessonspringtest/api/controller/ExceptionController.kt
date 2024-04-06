package ru.otus.publiclessonspringtest.api.controller

import org.apache.coyote.BadRequestException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingPathVariableException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException
import ru.otus.publiclessonspringtest.api.dto.GenericResponse
import ru.otus.publiclessonspringtest.api.exceptions.AccessException
import ru.otus.publiclessonspringtest.api.exceptions.AlreadyPerformedException
import ru.otus.publiclessonspringtest.api.exceptions.NotFoundException

@ControllerAdvice
class ExceptionController: ResponseEntityExceptionHandler() {

    override fun handleNoResourceFoundException(
        ex: NoResourceFoundException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleError(HttpStatus.BAD_REQUEST, data = mapOf("error" to ex.message))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        return handleError(HttpStatus.BAD_REQUEST, data = mapOf("error" to ex.message))
    }

    override fun handleMissingServletRequestParameter(
        ex: MissingServletRequestParameterException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleError(HttpStatus.BAD_REQUEST, data = mapOf("error" to ex.message))
    }

    override fun handleMissingPathVariable(
        ex: MissingPathVariableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleError(HttpStatus.BAD_REQUEST, data = mapOf("error" to ex.message))
    }

    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        return handleError(HttpStatus.BAD_REQUEST, data = mapOf("error" to ex.message))
    }

    @ExceptionHandler(AccessException::class)
    fun handleAccessException(ex: Exception): ResponseEntity<Any> {
        return handleError(HttpStatus.FORBIDDEN, data = mapOf("error" to "access denied"))
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(ex: BadRequestException): ResponseEntity<Any> {
        return handleError(HttpStatus.BAD_REQUEST, data = mapOf("error" to ex.message))
    }

    @ExceptionHandler(AlreadyPerformedException::class)
    fun handleAlreadyPerformed(ex: Exception): ResponseEntity<Any> {
        return handleError(HttpStatus.CONFLICT, data = mapOf("error" to "already performed"))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(ex: Exception): ResponseEntity<Any> {
        return handleError(HttpStatus.NOT_FOUND, data = mapOf("error" to "not found"))
    }

    private fun <T>handleError(httpStatus: HttpStatus, data: T): ResponseEntity<Any> {
        val responseBody = GenericResponse(isSuccess = false, data = data)
        return ResponseEntity(responseBody, httpStatus)
    }

}
