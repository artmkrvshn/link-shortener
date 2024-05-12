package me.link.unit

import me.link.util.Base62KeyGenerator
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.test.assertEquals

class Base62KeyGeneratorTest {

    private val generator = Base62KeyGenerator()

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2, 3, 5, 10, 100])
    fun `given positive length - when generate - then return generated string`(length: Int) {
        val generatedKey = generator.generate(length)
        assertEquals(length, generatedKey.length)
    }

    @ParameterizedTest
    @ValueSource(ints = [-1, -2, -3, -5, -10, -100])
    fun `given negative length - when generate - then throw IllegalArgumentException`(length: Int) {
        assertThrows<IllegalArgumentException> { generator.generate(length) }
    }
}