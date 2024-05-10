package me.link.util

import me.link.exception.BadURLException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

@Component
class URLValidator {

    private val log = LoggerFactory.getLogger(this::class.java)

    fun validate(url: String) {
        val status = getResponseStatus(url)
        if (status.isError) {
            val message = "URL $url returned status code ${status.value()}"
            log.warn(message)
            throw BadURLException(message)
        }
    }

    private fun getResponseStatus(url: String): HttpStatus {
        try {
            val httpConnection = URI.create(url.trim()).toURL().openConnection() as HttpURLConnection
            return HttpStatus.valueOf(httpConnection.responseCode)
        } catch (e: IOException) {
            log.warn("Invalid URL. ${e.message}")
            throw BadURLException("Invalid URL. ${e.message}", e)
        }
    }
}