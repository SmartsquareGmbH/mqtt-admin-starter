package de.smartsquare.starter.mqttadmin.client

import de.smartsquare.starter.mqttadmin.client.ClientActionResult.Success
import org.slf4j.LoggerFactory

class ClientService(private val brokerApiClient: BrokerApiClient) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun registerClient(
        clientId: String,
        password: String,
        vararg aclRule: AclRule
    ): ClientActionResult {
        val result = brokerApiClient.registerClient(ClientData(clientId, password), *aclRule)

        if (result is Success && aclRule.isEmpty()) {
            logger.debug("Registered client $clientId without any acl rule.")
        }

        return result
    }

    fun unregisterClient(clientId: String): ClientActionResult {
        return brokerApiClient.unregisterClient(clientId)
    }

    fun addAclRules(vararg aclRule: AclRule): ClientActionResult {
        return brokerApiClient.addAclRules(*aclRule)
    }

    fun deleteAclRules(clientId: String, topic: String): ClientActionResult {
        return brokerApiClient.deleteAclRules(clientId, topic)
    }
}
