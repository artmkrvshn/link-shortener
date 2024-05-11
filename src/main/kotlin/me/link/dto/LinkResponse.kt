package me.link.dto

import me.link.entity.Link
import java.time.LocalDateTime

data class LinkResponse(
    val key: String,
    val url: String,
    val createdAt: LocalDateTime
) {
    constructor(link: Link) : this(link.key, link.url, link.createdAt)
}