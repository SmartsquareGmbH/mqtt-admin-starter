package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EnableConfigurationProperties(EmqxApiProperties::class)
open class EmqxApiConfiguration {

    @Bean
    @Qualifier("emqx")
    open fun restTemplate(config: EmqxApiProperties): RestTemplate = RestTemplateBuilder()
        .rootUri("http://${config.host}:${config.port}")
        .basicAuthentication(config.username, config.password)
        .build()

    @Bean
    open fun emqxApiClient(
        @Qualifier("emqx") restTemplate: RestTemplate,
        objectMapper: ObjectMapper
    ) = EmqxApiClient(restTemplate, objectMapper)
}
