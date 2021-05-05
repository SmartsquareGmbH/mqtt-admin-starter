package de.smartsquare.starter.mqttadmin

import org.springframework.core.ParameterizedTypeReference

internal inline fun <reified T> typeRef() = object : ParameterizedTypeReference<T>() {}
