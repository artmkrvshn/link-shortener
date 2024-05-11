package me.link.repository

import me.link.entity.Link
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LinkRepositoryTest {

    companion object {
        @Container
        @ServiceConnection
        val postgresql = PostgreSQLContainer("postgres:16.2-alpine3.19")

        @BeforeAll
        @JvmStatic
        fun startDBContainer() {
            postgresql.start()
        }

        @AfterAll
        @JvmStatic
        fun stopDBContainer() {
            postgresql.stop()
        }
    }

    @Autowired
    private lateinit var repository: LinkRepository

    val links = listOf(
        Link(key = "key1", url = "https://example.com/1"),
        Link(key = "key2", url = "https://example.com/2"),
        Link(key = "key3", url = "https://example.com/3"),
        Link(key = "key4", url = "https://example.com/4"),
        Link(key = "key5", url = "https://example.com/5")
    )

    @BeforeEach
    fun setUp() {
        repository.saveAll(links)
    }

    @AfterEach
    fun tearDown() {
        repository.deleteAll()
    }

    @Test
    fun contextLoads() {
        assertThat(repository).isNotNull
        assertThat(postgresql).isNotNull
        assertThat(postgresql.isCreated).isTrue
        assertThat(postgresql.isRunning).isTrue
    }

    @Test
    fun `given valid key - when findByKey - then return Link`() {
        val link = links[2]

        val result: Link? = repository.findByKey(link.key)

        assertThat(result).isNotNull
        assertThat(result).isEqualTo(link)
    }

    @Test
    fun `given invalid key - when findByKey - then return null`() {
        val result: Link? = repository.findByKey("non_existent_key")

        assertThat(result).isNull()
    }

    @Test
    fun `given existent key - when existsByKey - then return true`() {
        val link = links[2]

        val result = repository.existsByKey(link.key)

        assertThat(result).isTrue
    }

    @Test
    fun `given non existent key - when existsByKey - then return false`() {
        val result = repository.existsByKey("non_existent_key")

        assertThat(result).isFalse
    }

}