package me.link.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.link.dto.LinkRequest
import me.link.entity.Link
import me.link.exception.BadURLException
import me.link.exception.KeyNotFoundException
import me.link.service.LinkServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.NOT_FOUND
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.mockito.Mockito.`when` as mockitoWhen

@WebMvcTest
@AutoConfigureMockMvc
class LinkControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: LinkServiceImpl

    private val jsonMapper = ObjectMapper()

    @Test
    fun contextLoads() {
        assertThat(mockMvc).isNotNull
        assertThat(service).isNotNull
    }

    @Test
    fun `given valid URL - when post Link - then return Link`() {
        val key = "key"
        val url = "https://example.com"

        val request = LinkRequest(url, null)
        val link = Link(key = key, url = url)

        val json: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(url)).thenReturn(link)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.key").value(key),
            jsonPath("$.url").value(url),
            jsonPath("$.createdAt").exists()
        )
        verify(service, times(1)).create(url)
    }

    @Test
    fun `given invalid URL - when post URL - then return Bad Request`() {
        val url = "https://example.com"
        val request = LinkRequest(url, null)
        val requestJson: String = jsonMapper.writeValueAsString(request)
        val exceptionMessage = "Invalid URL"

        mockitoWhen(service.create(url)).thenThrow(BadURLException::class.java)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest,
            jsonPath("$.title").value(BAD_REQUEST.reasonPhrase),
            jsonPath("$.status").value(BAD_REQUEST.value()),
            jsonPath("$.detail").value(exceptionMessage)
        )
        verify(service, times(1)).create(url)
    }

    @Test
    fun `given valid URL with valid custom key - when post Link - then return Link with custom key`() {
        val key = "key"
        val url = "https://example.com"
        val request = LinkRequest(url, key)
        val link = Link(key = key, url = url)
        val requestJson: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(url, key)).thenReturn(link)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.key").value(key),
            jsonPath("$.url").value(url),
            jsonPath("$.createdAt").exists()
        )
        verify(service, times(1)).create(url, key)
    }

    @Test
    fun `given valid URL with invalid custom key - when post Link - then return Bad Request`() {
        val key = "?//key#//"
        val url = "https://example.com"
        val request = LinkRequest(url, key)
        val requestJson: String = jsonMapper.writeValueAsString(request)
        val exceptionMessage = "Key should contain only letters, numbers, `-` and `_`"

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest,
            jsonPath("$.title").value(BAD_REQUEST.reasonPhrase),
            jsonPath("$.status").value(BAD_REQUEST.value()),
            jsonPath("$.detail").value(exceptionMessage)
        )
    }

    @Test
    fun `given invalid URL with valid custom key - when post Link - then return Bad Request`() {
        val key = "key"
        val url = "?//invalidURL#//.com"
        val request = LinkRequest(url, key)
        val requestJson: String = jsonMapper.writeValueAsString(request)
        val exceptionMessage = "Must be a valid URL"

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest,
            jsonPath("$.title").value(BAD_REQUEST.reasonPhrase),
            jsonPath("$.status").value(BAD_REQUEST.value()),
            jsonPath("$.detail").value(exceptionMessage)
        )
    }

    @Test
    fun `given invalid URL with invalid custom key - when post Link - then return Bad Request`() {
        val key = "?//key#//"
        val url = "?//invalidURL#//.com"
        val request = LinkRequest(url, key)
        val requestJson: String = jsonMapper.writeValueAsString(request)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isBadRequest,
            jsonPath("$.title").value(BAD_REQUEST.reasonPhrase),
            jsonPath("$.status").value(BAD_REQUEST.value()),
            jsonPath("$.detail").exists()
        )
    }

    @Test
    fun `given existing key - when get Link by key - then return RedirectView`() {
        val key = "validKey"
        val url = "https://example.com"
        val link = Link(key = key, url = url)

        mockitoWhen(service.getByKey(key)).thenReturn(link)

        mockMvc.perform(get("/api/v1/shortener/$key"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl(url))
    }

    @Test
    fun `given not existing key - when get Link by key - then return Not Found`() {
        val key = "notExistingKey"
        val exceptionMessage = "Link with key '$key' not found"

        mockitoWhen(service.getByKey(key)).thenThrow(KeyNotFoundException(exceptionMessage))

        mockMvc.perform(get("/api/v1/shortener/$key")).andExpectAll(
            status().is4xxClientError,
            status().isNotFound,
            jsonPath("$.title").value(NOT_FOUND.reasonPhrase),
            jsonPath("$.status").value(NOT_FOUND.value()),
            jsonPath("$.detail").value(exceptionMessage)
        )
    }

    @Test
    fun `given existing key - when get Link info - then return URL`() {
        val key = "validKey"
        val url = "https://example.com"
        val link = Link(key = key, url = url)

        mockitoWhen(service.getByKey(key)).thenReturn(link)

        mockMvc.perform(get("/api/v1/shortener/$key+")).andExpectAll(
            status().isOk,
            jsonPath("$.key").value(key),
            jsonPath("$.url").value(url),
            jsonPath("$.createdAt").exists()
        )
    }

    @Test
    fun `given not existing key - when get Link info - then return Not Found`() {
        val key = "notExistingKey"
        val exceptionMessage = "Link with key '$key' not found"

        mockitoWhen(service.getByKey(key)).thenThrow(KeyNotFoundException(exceptionMessage))

        mockMvc.perform(get("/api/v1/shortener/$key+")).andExpectAll(
            status().is4xxClientError,
            status().isNotFound,
            jsonPath("$.title").value(NOT_FOUND.reasonPhrase),
            jsonPath("$.status").value(NOT_FOUND.value()),
            jsonPath("$.detail").value(exceptionMessage)
        )
    }

}