package de.smartsquare.starter.mqttadmin.client

import org.springframework.stereotype.Service

@Service
class ClientService(private val brokerApiClient: BrokerApiClient) {

    fun registerClient(
        clientData: ClientData,
        aclRule: AclRule = generateDefaultAclRule(clientData.clientId)
    ): ClientActionResult {
        return brokerApiClient.registerClient(clientData, aclRule)
    }

    fun unregisterClient(clientId: String): ClientActionResult {
        return brokerApiClient.unregisterClient(clientId)
    }

    private fun generateDefaultAclRule(clientId: String) = AclRule(
        login = clientId,
        topic = "$clientId/#",
        action = AclRule.TopicAction.PUBSUB,
        allow = true
    )
}
