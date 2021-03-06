# Mqtt Admin Starter

This project contains a basic service to register and unregister clients to authenticate to an MQTT broker for Spring
Boot. As of the current version only support for EMQ X 4.3 is implemented, but other brokers could be implemented by
subclassing the `BrokerApiClient` interface.

> :warning: EMQ X regulary changes it's api so only use with the specified version!

## Getting started

### Gradle Configuration

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "de.smartsquare:mqtt-admin-starter:0.13.0"
}
```

### Application Properties

For EMQ X Broker:

```properties
emqx.api.host=localhost
emqx.api.port=18083
emqx.api.username=admin
emqx.api.password=public
```

### Docker Setup

For EMQ X Broker:

```yaml
# docker-compose.yml

version: '3'
services:
  emqx:
    image: emqx/emqx:4.2.10
    ports:
      - 1883:1883
      - 8081:8081
      - 18083:18083
    environment:
      - EMQX_LOADED_PLUGINS=emqx_management,emqx_dashboard,emqx_auth_mnesia
      - EMQX_ALLOW_ANONYMOUS=false
      - EMQX_ACL_NOMATCH=deny
```

### Service API

All service calls return a `ClientActionResult` which can be either `Success` or `Failure`.

```kotlin
class TestClientRegistration(private val clientService: ClientService) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun register() {
        // Registers a client to authenticate via username and password
        // Can consume a various number of acl rules:
        // AclRule(username: String, topic: String, action: TopicAction, allow: Boolean)
        // Where and action is PUB|SUB|PUBSUB.
        val result = clientService.registerClient(
            username = "username",
            password = "password"
        )

        if (result is Success) {
            logger.info("Successfully registered client!")
        } else {
            logger.error("Failed to register client.", result.error)
        }
    }

    fun unregister() {
        clientService.unregisterClient("username")
    }

    fun addAclRules() {
        // Adds a various number of acl rules.
        clientService.addAclRules(
            AclRule(
                username = "username",
                topic = "testTopic/#",
                action = TopicAction.PUB,
                allow = true
            )
        )
    }

    fun deleteAclRules() {
        // Deletes all acl rules for the client on the given topic.
        clientService.deleteAclRules(username = "username", topic = "testTopic/#")
    }
}
```
