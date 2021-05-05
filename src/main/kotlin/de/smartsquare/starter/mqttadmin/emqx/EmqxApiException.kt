package de.smartsquare.starter.mqttadmin.emqx

class EmqxApiException(
    val code: Int? = null,
    message: String? = null,
    error: Throwable? = null
) : RuntimeException(message, error)
