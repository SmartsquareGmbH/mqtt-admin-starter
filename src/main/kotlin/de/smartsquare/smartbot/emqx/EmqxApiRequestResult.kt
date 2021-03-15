package de.smartsquare.smartbot.emqx

/**
 *  [EMQ X HTTP API documentation](https://docs.emqx.io/en/broker/latest/advanced/http-api.html)
 */
data class EmqxApiRequestResult(

    /**
     *  The response can have a code which is always 0 in the success case or a defined value for some error cases.
     */
    val code: Int?,

    /**
     *  In some error cases the response has a message property.
     */
    val message: String?,

    /**
     *  A successful response with the code 0 can have a data property, containing the requested data or additional
     *  information.
     */
    val data: Any?
)
