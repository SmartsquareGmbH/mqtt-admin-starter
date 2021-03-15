package de.smartsquare.smartbot.client

import com.fasterxml.jackson.annotation.JsonProperty

data class ClientData(

    @JsonProperty("clientid")
    val clientId: String,

    val password: String,
)
