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

    private val clientData = ClientData(
        clientId = "testClient",
        password = "test"
    )

    @AfterEach
    fun cleanUp() {
        emqxApiClient.unregisterClient(clientData.clientId)
    }

    @Test
    fun `registers client`() {
        val result = clientService.registerClient(clientData)

        result.isSuccessful()
    }

    @Test
    fun `should not register client twice`() {
        clientService.registerClient(clientData)

        val result = clientService.registerClient(clientData)
        result.isNotSuccessful()
    }

    @Test
    fun `should unregister client`() {
        clientService.registerClient(clientData)

        val result = clientService.unregisterClient(clientData.clientId)
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
