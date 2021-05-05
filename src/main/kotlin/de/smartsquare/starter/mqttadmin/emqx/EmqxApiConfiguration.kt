package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import de.smartsquare.starter.mqttadmin.client.BrokerApiClient
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

@Configuration
@ConditionalOnProperty(prefix = "emqx.api", value = ["host"])
@EnableConfigurationProperties(EmqxApiProperties::class)
class EmqxApiConfiguration {

    @Bean
    @Qualifier("emqx")
    fun restTemplate(
        config: EmqxApiProperties,
        @Qualifier("emqx") objectMapper: ObjectMapper
    ): RestTemplate = RestTemplateBuilder()
        .rootUri("${config.schema}://${config.host}:${config.port}")
        .basicAuthentication(config.username, config.password)
        .messageConverters(MappingJackson2HttpMessageConverter(objectMapper))
        .build()

    @Bean
    fun emqxApiClient(@Qualifier("emqx") restTemplate: RestTemplate): BrokerApiClient = EmqxApiClient(restTemplate)

    @Bean
    @Qualifier("emqx")
    fun emqxObjectMapper(): ObjectMapper = jacksonObjectMapper()
        .findAndRegisterModules()
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
}
