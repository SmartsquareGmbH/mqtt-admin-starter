package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.smartsquare.starter.mqttadmin.client.BrokerApiClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
@ConditionalOnProperty(prefix = "emqx.api", value = ["host"])
@EnableConfigurationProperties(EmqxApiProperties::class)
class EmqxApiConfiguration {

    @Bean
    fun emqxApiClient(config: EmqxApiProperties): BrokerApiClient {
        val objectMapper = jacksonObjectMapper()
            .findAndRegisterModules()
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

        val restTemplate = RestTemplateBuilder()
            .rootUri("${config.schema}://${config.host}:${config.port}")
            .basicAuthentication(config.username, config.password)
            .messageConverters(MappingJackson2HttpMessageConverter(objectMapper))
            .build()

        return EmqxApiClient(restTemplate)
    }
}
