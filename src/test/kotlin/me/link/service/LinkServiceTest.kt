package me.link.service

import me.link.entity.Link
import me.link.exception.BadURLException
import me.link.exception.KeyNotFoundException
import me.link.repository.LinkRepository
import me.link.util.Generator
import me.link.util.URLValidator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when` as mockitoWhen

@ExtendWith(MockitoExtension::class)
class LinkServiceTest {

    @Mock
    private lateinit var linkRepository: LinkRepository

    @Mock
    private lateinit var keyGenerator: Generator<String>

    @Mock
    private lateinit var validator: URLValidator

    @InjectMocks
    private lateinit var linkService: LinkServiceImpl

    @Test
    fun contextLoads() {
        assertThat(linkService).isNotNull
        assertThat(linkRepository).isNotNull
        assertThat(keyGenerator).isNotNull
        assertThat(validator).isNotNull
    }

    @Test
    fun `given valid key - when getByKey - then return link`() {
        val key = "validKey"
        val url = "https://example.com"
        val link = Link(1, key, url)

        mockitoWhen(linkRepository.findByKey(key)).thenReturn(link)

        val result = linkService.getByKey(key)

        assertEquals(link, result)
        verify(validator, times(0)).validate(url)
        verify(linkRepository, times(1)).findByKey(key)
        verify(linkRepository, times(0)).existsByKey(key)
        verify(keyGenerator, times(0)).generate(anyInt())
    }

    @Test
    fun `given invalid key - when getByKey - then throw KeyNotFoundException`() {
        val key = "invalidKey"

        mockitoWhen(linkRepository.findByKey(key)).thenThrow(KeyNotFoundException::class.java)

        assertThrows<KeyNotFoundException> { linkService.getByKey(key) }
        verify(linkRepository, times(1)).findByKey(key)
        verify(linkRepository, times(0)).existsByKey(key)
        verify(linkRepository, times(0)).save(any())
    }

    @Test
    fun `given valid URL - when create - then return Link`() {
        val url = "https://www.example.org/"
        val key = "custom-key"
        val link = Link(1, key, url)

        mockitoWhen(linkRepository.save(any())).thenReturn(link)
        mockitoWhen(keyGenerator.generate(anyInt())).thenReturn(key)

        val result = linkService.create(url)

        assertEquals(result, link)
        verify(validator, times(1)).validate(url)
        verify(linkRepository, times(0)).existsByKey(key)
        verify(linkRepository, times(1)).save(any())
        verify(keyGenerator, times(1)).generate(anyInt())
    }

    @Test
    fun `given invalid URL - when create - then throw BadURLException`() {
        val invalidUrl = "invalid://www.wikipedia.org/"
        val key = "key"
        val link = Link(1, key, invalidUrl)

        mockitoWhen(linkRepository.existsByKey(key)).thenReturn(false)
        mockitoWhen(linkRepository.save(any())).thenReturn(link)

        val result = linkService.create(invalidUrl, key)

        assertEquals(result, link)
        verify(validator, times(1)).validate(invalidUrl)
        verify(linkRepository, times(1)).existsByKey(key)
        verify(linkRepository, times(1)).save(any())
        verify(keyGenerator, times(0)).generate(anyInt())
    }

    @Test
    fun `given not existing URL - when create - then throw BadURLException`() {
        val url = "https://www.example.org/"
        val key = "custom-key"
        val link = Link(1, key, url)

        mockitoWhen(linkRepository.existsByKey(key)).thenReturn(false)
        mockitoWhen(linkRepository.save(any())).thenReturn(link)

        val result = linkService.create(url, key)

        assertEquals(result, link)
        verify(validator, times(1)).validate(url)
        verify(linkRepository, times(1)).existsByKey(key)
        verify(linkRepository, times(1)).save(any())
        verify(keyGenerator, times(0)).generate(anyInt())
    }

    @Test
    fun `given valid URL with custom key - when create - then return Link`() {
        val url = "https://www.example.org/"
        val key = "custom-key"
        val link = Link(1, key, url)

        mockitoWhen(linkRepository.existsByKey(key)).thenReturn(false)
        mockitoWhen(linkRepository.save(any())).thenReturn(link)

        val result = linkService.create(url, key)

        assertEquals(result, link)
        verify(validator, times(1)).validate(url)
        verify(linkRepository, times(1)).existsByKey(key)
        verify(linkRepository, times(1)).save(any())
        verify(keyGenerator, times(0)).generate(anyInt())
    }

    @Test
    fun `given invalid URL with custom key - when create - then throw BadURLException`() {
        val invalidUrl = "invalid://www.example.org/"
        val key = "custom-key"

        mockitoWhen(linkRepository.existsByKey(key)).thenReturn(false)
        mockitoWhen(validator.validate(invalidUrl)).thenThrow(BadURLException::class.java)

        assertThrows<BadURLException> { linkService.create(invalidUrl, key) }

        verify(validator, times(1)).validate(invalidUrl)
        verify(linkRepository, times(1)).existsByKey(key)
        verify(linkRepository, times(0)).save(any())
        verify(keyGenerator, times(0)).generate(anyInt())
    }

    @Test
    fun `given not existing URL with custom key - when create - then throw BadURLException`() {
        val url = "https://www.example.org/"
        val key = "custom-key"

        mockitoWhen(linkRepository.existsByKey(key)).thenReturn(false)
        mockitoWhen(validator.validate(url)).thenThrow(BadURLException::class.java)

        assertThrows<BadURLException> { linkService.create(url, key) }

        verify(validator, times(1)).validate(url)
        verify(linkRepository, times(1)).existsByKey(key)
        verify(linkRepository, times(0)).save(any())
        verify(keyGenerator, times(0)).generate(anyInt())
    }
}