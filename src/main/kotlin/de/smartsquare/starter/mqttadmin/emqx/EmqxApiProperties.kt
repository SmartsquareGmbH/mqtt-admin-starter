package de.smartsquare.starter.mqttadmin.emqx

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties(prefix = "emqx.api")
@ConstructorBinding
data class EmqxApiProperties(
    val host: String,
    val port: String,
    val username: String,
    val password: String
)
