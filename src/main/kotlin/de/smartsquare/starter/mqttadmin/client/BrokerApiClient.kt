package de.smartsquare.starter.mqttadmin.client

interface BrokerApiClient {
    fun getClientRegistrations(): ClientResult<List<ClientRegistration>>
    fun registerClient(clientData: ClientData, vararg aclRule: AclRule): ClientActionResult
    fun unregisterClient(clientId: String): ClientActionResult
    fun addAclRules(vararg aclRule: AclRule): ClientActionResult
    fun deleteAclRules(clientId: String, topic: String): ClientActionResult
}
