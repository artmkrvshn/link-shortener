package me.link.handler

import me.link.exception.BadURLException
import me.link.exception.KeyAlreadyExistsException
import me.link.exception.KeyNotFoundException
import org.postgresql.util.PSQLException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class ExceptionHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ErrorResponse {
        log.warn("Validation failed: ${ex.bindingResult.allErrors}")
        return ErrorResponse.create(ex, BAD_REQUEST, ex.fieldError?.defaultMessage ?: "Invalid argument")
    }

    @ExceptionHandler(BadURLException::class)
    fun handleBadURLException(ex: BadURLException): ErrorResponse {
        log.warn("Bad URL: ${ex.message}")
        return ErrorResponse.create(ex, BAD_REQUEST, ex.message ?: "Invalid URL")
    }

    @ExceptionHandler(KeyAlreadyExistsException::class)
    fun handleKeyAlreadyExistsException(ex: KeyAlreadyExistsException): ErrorResponse {
        log.info("Key already exists: ${ex.message}")
        return ErrorResponse.create(ex, CONFLICT, ex.message ?: "Key already exists")
    }

    @ExceptionHandler(KeyNotFoundException::class)
    fun handleKeyNotFoundException(ex: KeyNotFoundException): ErrorResponse {
        log.info("Key not found: ${ex.message}")
        return ErrorResponse.create(ex, NOT_FOUND, ex.message ?: "Key wasn't found")
    }

    @ExceptionHandler(PSQLException::class)
    fun handlePSQLException(ex: PSQLException): ErrorResponse {
        log.error("Database error occurred: ${ex.message}")
        return ErrorResponse.create(ex, INTERNAL_SERVER_ERROR, "Sorry, something went wrong. Try again later")
    }
}