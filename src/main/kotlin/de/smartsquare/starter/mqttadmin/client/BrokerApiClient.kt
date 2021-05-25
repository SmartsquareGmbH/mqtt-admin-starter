package de.smartsquare.starter.mqttadmin.client

interface BrokerApiClient {
    fun getClientRegistrations(): ClientResult<List<ClientRegistration>>
    fun registerClient(clientData: ClientData, vararg aclRule: AclRule): ClientActionResult
    fun unregisterClient(username: String): ClientActionResult
    fun addAclRules(vararg aclRule: AclRule): ClientActionResult
    fun deleteAclRules(username: String, topic: String): ClientActionResult
}
