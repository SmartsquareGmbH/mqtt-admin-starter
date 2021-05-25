package de.smartsquare.starter.mqttadmin.client

import de.smartsquare.starter.mqttadmin.client.ClientActionResult.Success
import org.slf4j.LoggerFactory

class ClientService(private val brokerApiClient: BrokerApiClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun getClientRegistrations(): ClientResult<List<ClientRegistration>> {
        return brokerApiClient.getClientRegistrations()
    }

    fun registerClient(
        username: String,
        password: String,
        vararg aclRule: AclRule
    ): ClientActionResult {
        val result = brokerApiClient.registerClient(ClientData(username, password), *aclRule)

        if (result is Success && aclRule.isEmpty()) {
            logger.debug("Registered client $username without any acl rule.")
        }

        return result
    }

    fun unregisterClient(username: String): ClientActionResult {
        return brokerApiClient.unregisterClient(username)
    }

    fun addAclRules(vararg aclRule: AclRule): ClientActionResult {
        return brokerApiClient.addAclRules(*aclRule)
    }

    fun deleteAclRules(username: String, topic: String): ClientActionResult {
        return brokerApiClient.deleteAclRules(username, topic)
    }
}
