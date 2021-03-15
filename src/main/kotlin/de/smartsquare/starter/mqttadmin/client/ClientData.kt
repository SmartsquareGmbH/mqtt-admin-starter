package de.smartsquare.starter.mqttadmin.client

import com.fasterxml.jackson.annotation.JsonProperty

data class ClientData(

    @JsonProperty("clientid")
    val clientId: String,

    val password: String,
)
