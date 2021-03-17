# Mqtt Admin Starter

This project contains a basic service to register and unregister clients to authenticate to an MQTT broker.

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
    build:
      context: emqx
    ports:
      - 1883:1883
      - 8081:8081
      - 18083:18083
    environment:
      - EMQX_LOADED_PLUGINS=emqx_management,emqx_dashboard,emqx_auth_clientid,emqx_auth_mnesia
      - EMQX_AUTH__MNESIA__AS=clientid
```

Or:
```shell
docker run -d --name emqx -p 1883:1883 -p 8081:8081 -p 18083:18083 \
    -e EMQX_LOADED_PLUGINS="emqx_management,emqx_dashboard,emqx_auth_clientid,emqx_auth_mnesia" \
    -e EMQX_AUTH__MNESIA__AS="clientid" \
    emqx/emqx:4.2.7
```

### Service API

```kotlin
class TestClientRegistration(private val clientService: ClientService) {
    
    fun register() {
        
        // Registers a client to authenticate via clientId and password. 
        // Can consume a various number of acl rules: 
        // AclRule(login: String, topic: String, action: String, allow: Boolean)
        // Where login is some clientId and action is "pub"|"sub"|"pubsub"
        // Returns RegistrationResult(success: Boolean, message: String?)
        clientService.registerClient(
            clientId = "testClientId",
            password = "password"
        )
    }
    
    fun unregister() {
        
        // Returns RegistrationResult(success: Boolean, message: String?)
        clientService.unregisterClient("testClientId") 
    }
}
```