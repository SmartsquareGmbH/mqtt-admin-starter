package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.databind.ObjectMapper
import de.smartsquare.starter.mqttadmin.client.AclRule
import de.smartsquare.starter.mqttadmin.client.BrokerApiClient
import de.smartsquare.starter.mqttadmin.client.ClientActionResult
import de.smartsquare.starter.mqttadmin.client.ClientData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForObject
import java.net.URLEncoder

open class EmqxApiClient(
    @Qualifier("emqx") private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : BrokerApiClient {

    private val authClientUrl = "/api/v4/auth_clientid"
    private val aclRuleUrl = "/api/v4/mqtt_acl"

    override fun registerClient(clientData: ClientData, aclRule: AclRule): ClientActionResult {
        return try {
            post(authClientUrl, clientData)
            post(aclRuleUrl, aclRule.convertToRequestObject())

            ClientActionResult(success = true)
        } catch (e: RestClientException) {
            ClientActionResult(success = false, message = e.message)
        }
    }

    /**
     * @return a successful result even if the client is not registered
     */
    override fun unregisterClient(clientId: String): ClientActionResult {
        return try {
            restTemplate.delete("$authClientUrl/$clientId")

            val encodedTopic = URLEncoder.encode("$clientId/#", "UTF-8")
            restTemplate.delete("$aclRuleUrl/$clientId/$encodedTopic")

            ClientActionResult(success = true)
        } catch (e: RestClientException) {
            ClientActionResult(success = false, message = e.message)
        }
    }

    private fun post(url: String, data: Any) {
        val result = restTemplate.postForObject<EmqxApiRequestResult>(
            url = url,
            request = objectMapper.writeValueAsString(data)
        )

        evaluateResult(result)
    }

    private fun evaluateResult(result: EmqxApiRequestResult) {
        if (result.code == null || result.code != 0) {
            if (result.message != null) {
                throw RestClientException(result.message)
            } else {
                throw RestClientException("An error occurred.")
            }
        }
    }

    private data class AclRuleRequest(
        val login: String,
        val topic: String,
        val action: String,
        val allow: Boolean
    )

    private fun AclRule.convertToRequestObject() = AclRuleRequest(
        login = this.login,
        topic = this.topic,
        action = this.action.action,
        allow = this.allow
    )
}
