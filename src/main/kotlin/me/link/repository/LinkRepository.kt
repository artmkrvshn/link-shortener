package me.link.repository

import me.link.entity.Link
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface LinkRepository : JpaRepository<Link, String> {

    fun findByKey(@Param("key") key: String): Link?

    fun existsByKey(key: String): Boolean

}