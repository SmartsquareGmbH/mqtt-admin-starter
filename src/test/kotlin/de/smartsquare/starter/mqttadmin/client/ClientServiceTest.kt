package de.smartsquare.starter.mqttadmin.client

import de.smartsquare.starter.mqttadmin.EmqxInfrastructure
import de.smartsquare.starter.mqttadmin.client.AclRule.TopicAction.PUB
import de.smartsquare.starter.mqttadmin.client.AclRule.TopicAction.SUB
import de.smartsquare.starter.mqttadmin.client.ClientActionResult.Failure
import de.smartsquare.starter.mqttadmin.client.ClientActionResult.Success
import de.smartsquare.starter.mqttadmin.emqx.EmqxApiClient
import de.smartsquare.starter.mqttadmin.emqx.EmqxApiConfiguration
import org.amshove.kluent.shouldBe
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeInstanceOf
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldHaveSingleItem
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [EmqxApiConfiguration::class, ClientConfiguration::class])
class ClientServiceTest : EmqxInfrastructure() {

    @Autowired
    private lateinit var clientService: ClientService

    @Autowired
    private lateinit var emqxApiClient: EmqxApiClient

    private val username = "testusername"
    private val password = "test"

    private val aclRule = AclRule(
        login = username,
        topic = "testTopic/#",
        action = SUB,
        allow = true
    )

    @AfterEach
    fun cleanUp() {
        emqxApiClient.unregisterClient(username)
    }

    @Test
    fun `registers client without acl rules`() {
        val result = clientService.registerClient(username, password)

        result shouldBe Success
    }

    @Test
    fun `registers client with acl rules`() {
        val result = clientService.registerClient(username, password, aclRule, aclRule.copy(topic = "anotherTopic"))

        result shouldBe Success
    }

    @Test
    fun `should not register client twice`() {
        clientService.registerClient(username, password)

        val result = clientService.registerClient(username, password)
        result shouldBeInstanceOf Failure::class
    }

    @Test
    fun `should unregister client`() {
        clientService.registerClient(username, password)

        val result = clientService.unregisterClient(username)
        result shouldBe Success
    }

    @Test
    fun `should unregister client and delete single acl rule`() {
        clientService.registerClient(username, password, aclRule)

        val result = clientService.unregisterClient(username)
        result shouldBe Success
    }

    @Test
    fun `should unregister client and delete multiple acl rules`() {
        clientService.registerClient(username, password, aclRule, aclRule.copy(topic = "anotherTopic"))

        val result = clientService.unregisterClient(username)
        result shouldBe Success
    }

    @Test
    fun `adds acl rule`() {
        val result = clientService.addAclRules(aclRule)
        result shouldBe Success
    }

    @Test
    fun `adds multiple acl rules`() {
        val result = clientService.addAclRules(aclRule, aclRule.copy(topic = "anotherTopic"))
        result shouldBe Success
    }

    @Test
    fun `deletes all acl rules for a client on a topic`() {
        clientService.registerClient(
            username,
            password,
            aclRule.copy(action = SUB, allow = true),
            aclRule.copy(action = PUB, allow = false)
        )

        val result = clientService.deleteAclRules(username, aclRule.topic)
        result shouldBe Success
    }

    @Test
    fun `gets client registration`() {
        val registerResult = clientService.registerClient(username, password)
        registerResult shouldBe Success

        val result = clientService.getClientRegistrations()
        result.shouldBeInstanceOf<ClientResult.Success<*>>()
        result as ClientResult.Success
        result.data.shouldHaveSingleItem()
        result.data.first().login shouldBeEqualTo username
        result.data.first().superuser.shouldBeTrue()
    }

    @Test
    fun `gets multiple client registrations`() {
        clientService.registerClient(username, password)
        clientService.registerClient("test2", password)

        val result = clientService.getClientRegistrations()
        result.shouldBeInstanceOf<ClientResult.Success<*>>()
        result as ClientResult.Success
        result.data.shouldHaveSize(2)
    }
}
