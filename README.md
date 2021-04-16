# Mqtt Admin Starter

This project contains a basic service to register and unregister clients to authenticate to an MQTT broker.

## Getting started

### Gradle Configuration

```groovy
repositories {
  mavenCentral()
}

dependencies {
  implementation "de.smartsquare:mqtt-admin-starter:0.9.6"
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

```shell
# docker-compose.yml

version: '3'
services:
  emqx:
    image: emqx/emqx:4.2.7
    ports:
      - 1883:1883
      - 8081:8081
      - 18083:18083
    environment:
      - EMQX_LOADED_PLUGINS=emqx_management,emqx_dashboard,emqx_auth_clientid,emqx_auth_mnesia
      - EMQX_AUTH__MNESIA__AS=clientid
      - EMQX_ALLOW_ANONYMOUS=false
      - EMQX_ACL_NOMATCH=deny
    volumes:
      - ./acl.conf:/opt/emqx/etc/acl.conf:ro
```

```shell
# acl.conf

%% Allow "dashboard" users to subscribe to "$SYS/#" topics
{allow, {user, "dashboard"}, subscribe, ["$SYS/#"]}.

%% Allow users with IP address "127.0.0.1" to publish/subscribe to topics "$SYS/#", "#"
{allow, {ipaddr, "127.0.0.1"}, pubsub, ["$SYS/#", "#"]}.

%% Deny "All Users" subscribe to "$SYS/#" "#" Topics
{deny, all, subscribe, ["$SYS/#", {eq, "#"}]}.

%% Deny any other publish/subscribe operation
{deny, all}.
```

### Service API

```kotlin
class TestClientRegistration(private val clientService: ClientService) {
    
    fun register() {
        // Registers a client to authenticate via clientId and password
        // Can consume a various number of acl rules: 
        // AclRule(login: String, topic: String, action: TopicAction, allow: Boolean)
        // Where login is some clientId and action is PUB|SUB|PUBSUB
        // Returns ClientActionResult(success: Boolean, message: String?)
        clientService.registerClient(
            clientId = "testClientId",
            password = "password"
        )
    }
    
    fun unregister() {
        // Returns ClientActionResult(success: Boolean, message: String?)
        clientService.unregisterClient("testClientId") 
    }
    
    fun addAclRules() {
        // Adds a various number of acl rules
        // Returns ClientActionResult(success: Boolean, message: String?)
        clientService.addAclRules(
            AclRule(
                login = "testClientId",
                topic = "testTopic/#",
                action = TopicAction.PUB,
                allow = true
            )
        )
    }
    
    fun deleteAclRules() {
        // Deletes all acl rules for the client on the given topic
        // Returns ClientActionResult(success: Boolean, message: String?)
        clientService.deleteAclRules(clientId = "testClientId", topic = "testTopic/#")
    }
}
```