package de.smartsquare.starter.mqttadmin.emqx

import de.smartsquare.starter.mqttadmin.typeRef
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

internal class EmqxHttpClient(@Qualifier("emqx") private val restTemplate: RestTemplate) {

    internal fun <T> get(
        url: String,
        typeRef: ParameterizedTypeReference<EmqxApiRequestResult<T>> = typeRef(),
        vararg variables: Any
    ): T {
        val result = try {
            restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, typeRef, *variables)
        } catch (error: RestClientException) {
            throw EmqxApiException(message = "Failed to call EMQ X api $url", error = error)
        }

        evaluateResult(result)

        return result.body?.data
            ?: throw EmqxApiException(message = "Missing response data when calling EMQ X api $url")
    }

    internal fun post(url: String, data: Any, vararg variables: Any) {
        val result = try {
            val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_JSON }
            val body = HttpEntity(data, headers)

            restTemplate.exchange(url, HttpMethod.POST, body, typeRef<EmqxApiRequestResult<Any>>(), *variables)
        } catch (error: RestClientException) {
            throw EmqxApiException(message = "Failed to call EMQ X api $url", error = error)
        }

        evaluateResult(result)
    }

    internal fun delete(url: String, vararg variables: Any) {
        val result = try {
            restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                typeRef<EmqxApiRequestResult<Any>>(),
                *variables
            )
        } catch (error: RestClientException) {
            throw EmqxApiException(message = "Failed to call EMQ X api $url", error = error)
        }

        evaluateResult(result)
    }

    private fun <T> evaluateResult(result: ResponseEntity<EmqxApiRequestResult<T>>) {
        if (result.statusCode != HttpStatus.OK) {
            throw EmqxApiException(result.statusCodeValue, result.body.message)
        }

        if (result.body.code != 0) {
            throw EmqxApiException(result.body.code, result.body.message)
        }
    }
}
