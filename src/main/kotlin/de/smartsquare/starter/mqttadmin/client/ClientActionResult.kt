package de.smartsquare.starter.mqttadmin.client

sealed class ClientActionResult {
    object Success : ClientActionResult()

    data class Failure(
        val code: Int? = null,
        val message: String? = null,
        val error: Exception? = null
    ) : ClientActionResult()
}
