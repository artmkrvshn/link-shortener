package me.link.util

interface Generator<T> {

    fun generate(length: Int): T

}