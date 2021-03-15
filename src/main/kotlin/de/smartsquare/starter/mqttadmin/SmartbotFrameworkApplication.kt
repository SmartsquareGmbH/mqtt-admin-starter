package de.smartsquare.starter.mqttadmin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class SmartbotFrameworkApplication

fun main(args: Array<String>) {
    runApplication<SmartbotFrameworkApplication>(*args)
}
