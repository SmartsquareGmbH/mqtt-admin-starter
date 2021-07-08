package de.smartsquare.starter.mqttadmin.emqx

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.validation.annotation.Validated

@Validated
@ConstructorBinding
@ConfigurationProperties(prefix = "emqx.api")
data class EmqxApiProperties(

    /**
     * The schema to connect to.
     */
    val schema: String = "http",

    /**
     * The host to connect to.
     */
    val host: String,

    /**
     * The port to connect to.
     */
    val port: String,

    /**
     * The username to use.
     */
    val username: String,

    /**
     * The password to use.
     */
    val password: String
)
