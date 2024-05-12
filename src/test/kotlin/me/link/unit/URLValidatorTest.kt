package me.link.unit

import me.link.exception.BadURLException
import me.link.util.URLValidator
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource


class URLValidatorTest {

    private val validator = URLValidator()

    @ParameterizedTest
    @ValueSource(strings = ["https://www.wikipedia.org/", "https://www.youtube.com/", "https://github.com/", "https://en.wikipedia.org/wiki/Spring_Boot"])
    fun `given valid url - when validate - then return void`(url: String) {
        assertDoesNotThrow { validator.validate(url) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["sptth://www.wikipedia.org/", "https://www.youtube.com/404/", "https://github.com/dawd/d/awfawf/awf/awf/awf", "string"])
    fun `given invalid url - when validate - then throw BadURLException`(url: String) {
        assertThrows<BadURLException> { validator.validate(url) }
    }
}