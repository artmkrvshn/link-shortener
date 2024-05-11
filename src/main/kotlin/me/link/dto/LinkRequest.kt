package me.link.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

data class LinkRequest(

    @field:URL(message = "Must be a valid URL")
    @field:NotNull(message = "URL is mandatory")
    @field:NotBlank(message = "URL is mandatory")
    val url: String,

    @field:Size(min = 3, max = 30, message = "Key length must be between 3 and 30 characters")
    @field:Pattern(regexp = "^[a-zA-Z0-9-_]+\$", message = "Key should contain only letters, numbers, `-` and `_`")
    val customKey: String?
)