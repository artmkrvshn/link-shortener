package me.link.service

import me.link.entity.Link

interface LinkService {

    fun getByKey(key: String): Link

    fun create(url: String): Link

    fun create(url: String, key: String): Link

}