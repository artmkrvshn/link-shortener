package me.link.service

import me.link.entity.Link
import me.link.exception.KeyAlreadyExistsException
import me.link.exception.KeyNotFoundException
import me.link.repository.LinkRepository
import me.link.util.Generator
import me.link.util.URLValidator
import org.springframework.stereotype.Service

private const val DEFAULT_KEY_LENGTH = 6

@Service
class LinkServiceImpl(
    val repository: LinkRepository,
    val keyGenerator: Generator<String>,
    val validator: URLValidator
) : LinkService {

    override fun getByKey(key: String): Link {
        return repository.findByKey(key) ?: throw KeyNotFoundException("Link with key '$key' not found")
    }

    override fun create(url: String): Link {
        val key = keyGenerator.generate(DEFAULT_KEY_LENGTH)
        return saveUrl(url, key)
    }

    override fun create(url: String, key: String): Link {
        if (repository.existsByKey(key)) {
            throw KeyAlreadyExistsException("Key '$key' already exists")
        }
        return saveUrl(url, key)
    }

    fun saveUrl(url: String, key: String): Link {
        validator.validate(url)
        val link = Link(key = key, url = url)
        return repository.save(link)
    }
}