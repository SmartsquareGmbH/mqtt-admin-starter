package de.smartsquare.starter.mqttadmin.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.smartsquare.starter.mqttadmin.Infrastructure
import de.smartsquare.starter.mqttadmin.emqx.EmqxApiClient
import de.smartsquare.starter.mqttadmin.emqx.EmqxApiConfiguration
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@SpringBootTest(classes = [EmqxApiConfiguration::class, ClientConfiguration::class, ClientServiceTest.JacksonConfiguration::class])
class ClientServiceTest : Infrastructure() {

    @Autowired
    private lateinit var clientService: ClientService

    @Autowired
    private lateinit var emqxApiClient: EmqxApiClient

    private val clientId = "testClientId"
    private val password = "test"

    private val aclRule = AclRule(
        login = clientId,
        topic = "testTopic",
        action = AclRule.TopicAction.SUB,
        allow = true
    )

    @AfterEach
    fun cleanUp() {
        emqxApiClient.unregisterClient(clientId)
    }

    @Test
    fun `registers client without acl rules`() {
        val result = clientService.registerClient(clientId, password)

        result.isSuccessful()
    }

    @Test
    fun `registers client with acl rules`() {
        val result = clientService.registerClient(clientId, password, aclRule, aclRule.copy(topic = "anotherTopic"))

        result.isSuccessful()
    }

    @Test
    fun `should not register client twice`() {
        clientService.registerClient(clientId, password)

        val result = clientService.registerClient(clientId, password)
        result.isNotSuccessful()
    }

    @Test
    fun `should unregister client`() {
        clientService.registerClient(clientId, password)

        val result = clientService.unregisterClient(clientId)
        result.isSuccessful()
    }

    @Test
    fun `should unregister client and delete single acl rule`() {
        clientService.registerClient(clientId, password, aclRule)

        val result = clientService.unregisterClient(clientId)
        result.isSuccessful()
    }

    @Test
    fun `should unregister client and delete multiple acl rules`() {
        clientService.registerClient(clientId, password, aclRule, aclRule.copy(topic = "anotherTopic"))

        val result = clientService.unregisterClient(clientId)
        result.isSuccessful()
    }

    @Configuration
    open class JacksonConfiguration {

        @Bean
        open fun objectMapper(): ObjectMapper = jacksonObjectMapper().findAndRegisterModules()
    }

    private fun ClientActionResult.isSuccessful(): Boolean {
        return this.success.shouldBeTrue()
    }

    private fun ClientActionResult.isNotSuccessful(): Boolean {
        return this.success.shouldBeFalse()
    }
}
