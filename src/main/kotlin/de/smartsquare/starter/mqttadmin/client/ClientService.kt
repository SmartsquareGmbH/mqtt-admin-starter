package de.smartsquare.starter.mqttadmin.client

open class ClientService(private val brokerApiClient: BrokerApiClient) {

    fun registerClient(
        clientId: String,
        password: String,
        vararg aclRule: AclRule
    ): ClientActionResult {
        return brokerApiClient.registerClient(ClientData(clientId, password), *aclRule)
    }

    fun unregisterClient(clientId: String): ClientActionResult {
        return brokerApiClient.unregisterClient(clientId)
    }
}
