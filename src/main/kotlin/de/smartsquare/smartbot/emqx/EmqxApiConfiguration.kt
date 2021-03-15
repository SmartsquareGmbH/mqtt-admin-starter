package de.smartsquare.smartbot.emqx

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class EmqxApiConfiguration {

    @Bean
    @Qualifier("emqx")
    fun restTemplate(config: EmqxApiProperties): RestTemplate = RestTemplateBuilder()
        .rootUri("http://${config.host}:${config.port}")
        .basicAuthentication(config.username, config.password)
        .build()
}
