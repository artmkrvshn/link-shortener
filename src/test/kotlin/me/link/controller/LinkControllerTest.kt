package me.link.controller

import com.fasterxml.jackson.databind.ObjectMapper
import me.link.dto.LinkRequest
import me.link.entity.Link
import me.link.exception.BadURLException
import me.link.service.LinkServiceImpl
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.ErrorResponse
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
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

    /*
    @PostMapping
    fun create(@Valid @RequestBody request: LinkRequest): ResponseEntity<Link> {
        val link: Link = if (request.customKey.isNullOrBlank()) {
            service.create(request.url)
        } else {
            service.create(request.url, request.customKey)
        }
        return ResponseEntity.ok(link)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") key: String): RedirectView {
        val link = service.getByKey(key)
        return RedirectView(link.url)
    }

    @GetMapping("/{id}+")
    fun info(@PathVariable("id") key: String): ResponseEntity<Link> {
        val link = service.getByKey(key)
        return ResponseEntity.ok(link)
    }
     */

    @Test
    fun `given valid URL with custom key - when post url - then return Link with custom URL`() {
        val id = 1L
        val key = "key"
        val url = "https://example.com"
        val createdAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 5, 30))

        val request = LinkRequest(url, key)
        val link = Link(id, key, url, createdAt)

        val json: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(url, key)).thenReturn(link)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.id").value(id),
            jsonPath("$.key").value(key),
            jsonPath("$.url").value(url),
            jsonPath("$.createdAt").value(createdAt.toString())
        )
        verify(service, times(1)).create(url, key)
    }

    @Test
    fun `given invalid URL with custom key - when post url - then return ErrorResponse`() {
        val id = 1L
        val key = "key"
        val url = "https://example.com"
        val createdAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 5, 30))

        val request = LinkRequest(url, key)
        val link = Link(id, key, url, createdAt)

        val exception = BadURLException("Invalid URL")
        val errorResponse = ErrorResponse.create(exception, HttpStatus.BAD_REQUEST, "Invalid URL")

        val json: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(url, key)).thenThrow(BadURLException::class.java)

        mockMvc.perform(
            post("/api/v1/shortener")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo { println(it.resolvedException) }
            .andExpect { assertTrue(it.resolvedException is BadURLException) }
            .andExpectAll(
                status().isBadRequest,
                jsonPath("$.type").value(errorResponse.body.type),
                jsonPath("$.title").value(errorResponse.body.title),
                jsonPath("$.status").value(errorResponse.body.status),
                jsonPath("$.detail").value(errorResponse.body.detail)
            ).andExpect {
                jsonPath("$.instance").value(it.request.requestURL)
            }
        verify(service, times(1)).create(url, key)
    }

    /*
    {
    "type": "about:blank",
    "title": "Internal Server Error",
    "status": 500,
    "detail": "Sorry, something went wrong. Try again later",
    "instance": "/api/v1/shortener"
}
     */

    fun `given valid URL with invalid custom key - when post url - then return ErrorResponse`() {}

    fun `given valid URL - when post url - then return Link`() {}
    fun `given invalid URL - when post url - then return ErrorResponse`() {}

    fun `given valid key - when get link by key - then return RedirectView`() {}
    fun `given not existing key - when get link by key - then return ErrorResponse`() {}

    fun `given valid key - when get info by key - then return Link`() {}
    fun `given not existing key - when get info by key - then return ErrorResponse`() {
        val key = "random"

    }

    @Test
    fun `given valid key - when get Link - then return link`() {
        val key = "validKey"
        val url = "https://example.com"
        val link = Link(1, key, url)

        mockitoWhen(service.getByKey(key)).thenReturn(link)

        mockMvc.perform(get("/api/v1/shortener/$key"))
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl(url))
    }

    @Test
    fun `given invalid key - when get Link - then throw URLNotFoundException`() {
        val key = "invalidKey"

        mockitoWhen(service.getByKey(key)).thenThrow(BadURLException::class.java)

        mockMvc.perform(get("/api/v1/shortener/$key"))
            .andExpect(status().isNotFound)
    }


    @Test
    fun `given valid key - when get Link info - then return link entity`() {
        val key = "validKey"
        val url = "https://example.com"
        val createdAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 5, 30))
        val link = Link(1, key, url, createdAt)

        mockitoWhen(service.getByKey(key)).thenReturn(link)

        mockMvc.perform(get("/api/v1/shortener/$key+"))
            .andExpectAll(
                status().isOk,
                jsonPath("$.id").value(1),
                jsonPath("$.key").value(key),
                jsonPath("$.url").value(url),
                jsonPath("$.createdAt").value(createdAt.toString())
            )
    }

    @Test
    fun `given invalid key - when get Link info - then throw URLNotFoundException`() {
        val key = "invalidKey"

        mockitoWhen(service.getByKey(key)).thenThrow(BadURLException::class.java)

        mockMvc.perform(get("/api/v1/shortener/$key+"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `given LinkRequest with valid url - when post Link - then create Link`() {
        val id = 1L
        val key = "AKB381"
        val validUrl = "https://example.com"
        val createdAt = LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 5, 30))

        val request = LinkRequest(validUrl, null)
        val link = Link(id, key, validUrl, createdAt)

        val json: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(validUrl)).thenReturn(link)

        mockMvc.perform(
            post("/api/v1/shortener/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
            status().isOk,
            jsonPath("$.id").value(id),
            jsonPath("$.key").value(key),
            jsonPath("$.url").value(validUrl),
            jsonPath("$.createdAt").value(createdAt.toString())
        )

    }

    @Test
    fun `given LinkRequest with invalid url - when post Link - then throw URLException`() {
        val invalidUrl = "https://exam ple.com"

        val request = LinkRequest(invalidUrl, null)

        val json: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(invalidUrl)).thenThrow(BadURLException::class.java)

        mockMvc.perform(
            post("/api/v1/shortener/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        )
    }

    @Test
    fun `given LinkRequest with valid custom key - when post Link - then throw URLException`() {

    }

    @Test
    fun `given LinkRequest with invalid custom key - when post Link - then throw URLException`() {
        val url = "https://example.com"
        val invalidCustomKey = "super-key:)"

        val request = LinkRequest(url, invalidCustomKey)

        val json: String = jsonMapper.writeValueAsString(request)

        mockitoWhen(service.create(url, invalidCustomKey)).thenThrow(BadURLException::class.java)

        mockMvc.perform(
            post("/api/v1/shortener/")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)
        )
    }

}