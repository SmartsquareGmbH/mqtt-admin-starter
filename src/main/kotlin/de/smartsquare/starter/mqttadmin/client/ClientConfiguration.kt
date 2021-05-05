package de.smartsquare.starter.mqttadmin.client

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ClientConfiguration {

    @Bean
    fun clientService(brokerApiClient: BrokerApiClient) = ClientService(brokerApiClient)
}
