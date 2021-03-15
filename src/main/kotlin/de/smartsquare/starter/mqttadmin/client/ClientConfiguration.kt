package de.smartsquare.starter.mqttadmin.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ClientConfiguration {

    @Bean
    open fun clientService(brokerApiClient: BrokerApiClient) = ClientService(brokerApiClient)
}
