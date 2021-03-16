package de.smartsquare.starter.mqttadmin.client

interface BrokerApiClient {
    fun registerClient(clientData: ClientData, vararg aclRule: AclRule): ClientActionResult
    fun unregisterClient(clientId: String): ClientActionResult
}
