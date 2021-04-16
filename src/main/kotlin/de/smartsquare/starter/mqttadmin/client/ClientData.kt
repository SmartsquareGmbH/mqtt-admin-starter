package de.smartsquare.starter.mqttadmin.client

import com.fasterxml.jackson.annotation.JsonProperty

data class ClientData(

    @JsonProperty("username")
    val username: String,

    val password: String,
)
