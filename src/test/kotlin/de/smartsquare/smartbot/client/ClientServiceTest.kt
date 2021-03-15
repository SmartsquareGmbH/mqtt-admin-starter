package de.smartsquare.smartbot.client

import de.smartsquare.smartbot.Infrastructure
import de.smartsquare.smartbot.emqx.EmqxApiClient
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.junit.jupiter.Testcontainers

@SpringBootTest
@Testcontainers
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

    private fun ClientActionResult.isSuccessful(): Boolean {
        return this.success.shouldBeTrue()
    }

    private fun ClientActionResult.isNotSuccessful(): Boolean {
        return this.success.shouldBeFalse()
    }
}
