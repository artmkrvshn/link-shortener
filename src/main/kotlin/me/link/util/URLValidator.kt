package me.link.util

import me.link.exception.BadURLException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI

@Component
class URLValidator {

    fun validate(url: String) {
        val status = getResponseStatus(url)
        if (status.isError) {
            throw BadURLException("URL $url returned status code ${status.value()}")
        }
    }

    private fun getResponseStatus(url: String): HttpStatus {
        try {
            val httpConnection = URI.create(url.trim()).toURL().openConnection() as HttpURLConnection
            return HttpStatus.valueOf(httpConnection.responseCode)
        }  catch (e: IOException) {
            throw BadURLException("Invalid URL. ${e.message}", e)
        } catch (e: IllegalArgumentException) {
            throw BadURLException("Invalid URL. ${e.message}", e)
        }
    }
}