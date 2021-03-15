package de.smartsquare.smartbot.client

interface BrokerApiClient {
    fun registerClient(clientData: ClientData, aclRule: AclRule): ClientActionResult
    fun unregisterClient(clientId: String): ClientActionResult
}
