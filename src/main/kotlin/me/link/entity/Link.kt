package me.link.entity

import jakarta.persistence.*
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

@Entity
@Table(name = "links")
data class Link(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "key")
    val key: String,

    @Column(name = "url")
    val url: String,

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()

)