package de.smartsquare.starter.mqttadmin.client

data class AclRule(
    val login: String,
    val topic: String,
    val action: TopicAction,
    val allow: Boolean
) {
    enum class TopicAction(val action: String) {
        SUB("sub"), PUB("pub"), PUBSUB("pubsub")
    }
}
