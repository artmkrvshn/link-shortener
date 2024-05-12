package me.link.util

import org.springframework.stereotype.Component
import java.security.SecureRandom

private const val DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
private const val LENGTH = DIGITS.length
private val rnd = SecureRandom()

@Component
class Base62KeyGenerator : Generator<String> {

    override fun generate(length: Int): String {
        if (length < 0) throw IllegalArgumentException("Length must not be negative")
        val sb = StringBuilder(length)
        for (i in 0 until length) sb.append(DIGITS[rnd.nextInt(LENGTH)])
        return sb.toString()
    }

}