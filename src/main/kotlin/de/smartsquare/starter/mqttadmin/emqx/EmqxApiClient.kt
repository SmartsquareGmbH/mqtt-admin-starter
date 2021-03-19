package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.module.kotlin.treeToValue
import de.smartsquare.starter.mqttadmin.client.AclRule
import de.smartsquare.starter.mqttadmin.client.BrokerApiClient
import de.smartsquare.starter.mqttadmin.client.ClientActionResult
import de.smartsquare.starter.mqttadmin.client.ClientData
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import org.springframework.web.client.postForObject

open class EmqxApiClient(
    @Qualifier("emqx") private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper
) : BrokerApiClient {

    private val authClientUrl = "/api/v4/auth_clientid"
    private val aclRuleUrl = "/api/v4/mqtt_acl"

    override fun registerClient(clientData: ClientData, vararg aclRule: AclRule): ClientActionResult {
        return try {
            post(authClientUrl, clientData)

            if (aclRule.isNotEmpty()) {
                post(aclRuleUrl, aclRule.map { it.convertToTransferObject() })
            }

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

            deleteAclRules(clientId)

            ClientActionResult(success = true)
        } catch (e: RestClientException) {
            ClientActionResult(success = false, message = e.message)
        }
    }

    override fun addAclRules(vararg aclRule: AclRule): ClientActionResult {
        return try {
            if (aclRule.isNotEmpty()) {
                post(aclRuleUrl, aclRule.map { it.convertToTransferObject() })
            }

            ClientActionResult(success = true)
        } catch (e: RestClientException) {
            ClientActionResult(success = false, message = e.message)
        }
    }

    /**
     * @return a successful result even if the acl rule is not existing
     */
    override fun deleteAclRules(clientId: String, topic: String): ClientActionResult {
        return try {
            restTemplate.delete("$aclRuleUrl/$clientId/{topic}", topic)

            ClientActionResult(success = true)
        } catch (e: RestClientException) {
            ClientActionResult(success = false, message = e.message)
        }

    }

    private fun deleteAclRules(clientId: String) {
        val aclRules = getAclRules(clientId).data!!

        for (aclRule in aclRules) {
            restTemplate.delete("$aclRuleUrl/$clientId/{topic}", aclRule.topic)
        }
    }

    private fun post(url: String, data: Any) {
        val result = restTemplate.postForObject<EmqxApiRequestResult<Any>>(
            url = url,
            request = objectMapper.writeValueAsString(data)
        )

        evaluateResult(result)
    }

    private fun getAclRules(clientId: String): EmqxApiRequestResult<List<AclRuleDto>> {
        val result = restTemplate.getForObject<JsonNode>("$aclRuleUrl/$clientId")["data"]

        return when {
            result.nodeType == JsonNodeType.ARRAY -> {
                EmqxApiRequestResult(
                    code = 0,
                    data = result.mapNotNull { it.convertToAclRuleDto() })
            }
            result.nodeType == JsonNodeType.OBJECT && result.hasNonNull("topic") -> {
                EmqxApiRequestResult(
                    code = 0,
                    data = listOf(result.convertToAclRuleDto()!!)
                )
            }
            else -> {
                EmqxApiRequestResult(code = 0, data = emptyList())
            }
        }
    }

    private fun evaluateResult(result: EmqxApiRequestResult<Any>) {
        if (result.code == null || result.code != 0) {
            if (result.message != null) {
                throw RestClientException(result.message)
            } else {
                throw RestClientException("An error occurred.")
            }
        }
    }

    private data class AclRuleDto(
        val login: String,
        val topic: String,
        val action: String,
        val allow: Boolean
    )

    private fun AclRule.convertToTransferObject() = AclRuleDto(
        login = this.login,
        topic = this.topic,
        action = this.action.action,
        allow = this.allow
    )

    private fun JsonNode.convertToAclRuleDto() = objectMapper.treeToValue<AclRuleDto>(this)
}
