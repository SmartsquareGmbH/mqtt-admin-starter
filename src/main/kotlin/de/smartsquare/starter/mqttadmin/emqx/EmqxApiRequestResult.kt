package de.smartsquare.starter.mqttadmin.emqx

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 *  [EMQ X HTTP API documentation](https://docs.emqx.io/en/broker/latest/advanced/http-api.html)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class EmqxApiRequestResult<T>(

    /**
     *  The response can have a code which is always 0 in the success case or a defined value for some error cases.
     */
    val code: Int? = null,

    /**
     *  In some error cases the response has a message property.
     */
    val message: String? = null,

    /**
     *  A successful response with the code 0 can have a data property, containing the requested data or additional
     *  information.
     */
    val data: T? = null
)
