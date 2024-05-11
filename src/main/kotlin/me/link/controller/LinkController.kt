package me.link.controller

import jakarta.validation.Valid
import me.link.dto.LinkRequest
import me.link.dto.LinkResponse
import me.link.entity.Link
import me.link.service.LinkServiceImpl
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.view.RedirectView

@RestController
@RequestMapping("/api/v1/shortener")
class LinkController(private val service: LinkServiceImpl) {

    @PostMapping
    fun create(
        @Valid @RequestBody request: LinkRequest
    ): ResponseEntity<LinkResponse> {
        val link: Link = if (request.customKey.isNullOrBlank()) {
            service.create(request.url)
        } else {
            service.create(request.url, request.customKey)
        }
        val response = LinkResponse(link)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun get(@PathVariable("id") key: String): RedirectView {
        val link = service.getByKey(key)
        return RedirectView(link.url)
    }

    @GetMapping("/{id}+")
    fun info(@PathVariable("id") key: String): ResponseEntity<LinkResponse> {
        val link = service.getByKey(key)
        val response = LinkResponse(link)
        return ResponseEntity.ok(response)
    }
}