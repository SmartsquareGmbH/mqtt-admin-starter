package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import de.smartsquare.starter.mqttadmin.client.AclRule
import de.smartsquare.starter.mqttadmin.client.BrokerApiClient
import de.smartsquare.starter.mqttadmin.client.ClientActionResult
import de.smartsquare.starter.mqttadmin.client.ClientData
import de.smartsquare.starter.mqttadmin.client.ClientRegistration
import de.smartsquare.starter.mqttadmin.client.ClientResult
import de.smartsquare.starter.mqttadmin.emqx.EmqxApiClient.EmqxAclRule.EmqxAccess.ALLOW
import de.smartsquare.starter.mqttadmin.emqx.EmqxApiClient.EmqxAclRule.EmqxAccess.DENY
import de.smartsquare.starter.mqttadmin.typeRef
import org.springframework.web.client.RestTemplate

class EmqxApiClient(restTemplate: RestTemplate) : BrokerApiClient {

    private companion object {
        private const val authClientUrl = "/api/v4/auth_username"
        private const val aclRuleUrl = "/api/v4/acl"
    }

    private val emqxHttpClient = EmqxHttpClient(restTemplate)

    override fun getClientRegistrations(): ClientResult<List<ClientRegistration>> {
        return try {
            val result = emqxHttpClient.get(
                authClientUrl,
                typeRef<EmqxApiRequestResult<List<EmqxClientRegistration>>>()
            )

            // EMQ X currently only supports getting the username.
            ClientResult.Success(result.map { ClientRegistration(it.username, null, true) })
        } catch (e: EmqxApiException) {
            ClientResult.Failure(code = e.code, message = e.message, error = e)
        }
    }

    override fun registerClient(clientData: ClientData, vararg aclRule: AclRule): ClientActionResult {
        return try {
            emqxHttpClient.post(authClientUrl, clientData)

            if (aclRule.isNotEmpty()) {
                emqxHttpClient.post(aclRuleUrl, aclRule.map { it.forEmqx() })
            }

            ClientActionResult.Success
        } catch (e: EmqxApiException) {
            ClientActionResult.Failure(code = e.code, message = e.message, error = e)
        }
    }

    /**
     * @return a successful result even if the client is not registered
     */
    override fun unregisterClient(username: String): ClientActionResult {
        return try {
            emqxHttpClient.delete("$authClientUrl/{username}", username)
            deleteAclRules(username)

            ClientActionResult.Success
        } catch (e: EmqxApiException) {
            ClientActionResult.Failure(code = e.code, message = e.message, error = e)
        }
    }

    override fun addAclRules(vararg aclRule: AclRule): ClientActionResult {
        return try {
            if (aclRule.isNotEmpty()) {
                emqxHttpClient.post(aclRuleUrl, aclRule.map { it.forEmqx() })
            }

            ClientActionResult.Success
        } catch (e: EmqxApiException) {
            ClientActionResult.Failure(code = e.code, message = e.message, error = e)
        }
    }

    /**
     * @return a successful result even if the acl rule is not existing
     */
    override fun deleteAclRules(username: String, topic: String): ClientActionResult {
        return try {
            emqxHttpClient.delete("$aclRuleUrl/username/{username}/topic/{topic}", username, topic)

            ClientActionResult.Success
        } catch (e: EmqxApiException) {
            ClientActionResult.Failure(code = e.code, message = e.message, error = e)
        }
    }

    private fun deleteAclRules(username: String) {
        val topics = getAclRuleTopics(username)

        for (topic in topics) {
            emqxHttpClient.delete("$aclRuleUrl/username/{username}/topic/{topic}", username, topic)
        }
    }

    private fun getAclRuleTopics(username: String): List<String> {
        return emqxHttpClient
            .get("$aclRuleUrl/username/{username}", typeRef<EmqxApiRequestResult<List<EmqxAclTopic>>>(), username)
            .mapNotNull { it.topic }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class EmqxAclTopic(val topic: String?)

    private data class EmqxClientRegistration(val username: String)

    private data class EmqxAclRule(
        val username: String,
        val topic: String,
        val action: String,
        val access: EmqxAccess
    ) {
        enum class EmqxAccess {
            @JsonProperty("allow")
            ALLOW,

            @JsonProperty("deny")
            DENY
        }
    }

    private fun AclRule.forEmqx() = EmqxAclRule(
        username = this.username,
        topic = this.topic,
        action = this.action.action,
        access = if (this.allow) ALLOW else DENY
    )
}
