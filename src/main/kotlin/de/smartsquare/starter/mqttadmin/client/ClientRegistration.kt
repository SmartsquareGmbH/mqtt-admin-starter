package de.smartsquare.starter.mqttadmin.client

data class ClientRegistration(
    val login: String,
    val password: String?,
    val superuser: Boolean,
)
