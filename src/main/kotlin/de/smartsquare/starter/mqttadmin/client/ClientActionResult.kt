package de.smartsquare.starter.mqttadmin.client

sealed class ClientResult<out T> {
    data class Success<out T>(val data: T) : ClientResult<T>()

    data class Failure(
        val code: Int? = null,
        val message: String? = null,
        val error: Exception? = null
    ) : ClientResult<Nothing>()
}

sealed class ClientActionResult {
    object Success : ClientActionResult()

    data class Failure(
        val code: Int? = null,
        val message: String? = null,
        val error: Exception? = null
    ) : ClientActionResult()
}
