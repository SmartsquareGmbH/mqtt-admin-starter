package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import de.smartsquare.starter.mqttadmin.client.AclRule
import de.smartsquare.starter.mqttadmin.client.BrokerApiClient
import de.smartsquare.starter.mqttadmin.client.ClientActionResult
import de.smartsquare.starter.mqttadmin.client.ClientData
import de.smartsquare.starter.mqttadmin.typeRef
import org.springframework.web.client.RestTemplate

class EmqxApiClient(restTemplate: RestTemplate) : BrokerApiClient {

    private companion object {
        private const val authClientUrl = "/api/v4/auth_username"
        private const val aclRuleUrl = "/api/v4/mqtt_acl"
    }

    private val emqxHttpClient = EmqxHttpClient(restTemplate)

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
    override fun unregisterClient(clientId: String): ClientActionResult {
        return try {
            emqxHttpClient.delete("$authClientUrl/$clientId")
            deleteAclRules(clientId)

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
    override fun deleteAclRules(clientId: String, topic: String): ClientActionResult {
        return try {
            emqxHttpClient.delete("$aclRuleUrl/$clientId/{topic}", topic)

            ClientActionResult.Success
        } catch (e: EmqxApiException) {
            ClientActionResult.Failure(code = e.code, message = e.message, error = e)
        }
    }

    private fun deleteAclRules(clientId: String) {
        val topics = getAclRuleTopics(clientId)

        for (topic in topics) {
            emqxHttpClient.delete("$aclRuleUrl/$clientId/{topic}", topic)
        }
    }

    private fun getAclRuleTopics(clientId: String): List<String> {
        return emqxHttpClient
            .get("$aclRuleUrl/$clientId", typeRef<EmqxApiRequestResult<List<EmqxAclTopic>>>())
            .mapNotNull { it.topic }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class EmqxAclTopic(val topic: String?)

    private data class EmqxAclRule(
        val login: String,
        val topic: String,
        val action: String,
        val allow: Boolean
    )

    private fun AclRule.forEmqx() = EmqxAclRule(
        login = this.login,
        topic = this.topic,
        action = this.action.action,
        allow = this.allow
    )
}
