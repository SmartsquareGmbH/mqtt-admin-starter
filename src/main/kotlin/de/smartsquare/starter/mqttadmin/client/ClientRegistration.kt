package de.smartsquare.starter.mqttadmin.client

data class ClientRegistration(
    val username: String,
    val password: String?,
    val superuser: Boolean,
)
