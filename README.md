# Smartbot Framework (Mqtt Admin Starter)

This project contains a basic service to register and unregister clients to authenticate to an MQTT broker.

### Application Properties
For EMQ X Broker:

```properties
emqx.api.host=host
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
```

```dockerfile
# Dockerfile

FROM emqx/emqx:4.2.7
COPY emqx_auth_mnesia.conf /opt/emqx/etc/plugins/emqx_auth_mnesia.conf
ENV EMQX_LOADED_PLUGINS emqx_management,emqx_dashboard,emqx_auth_clientid,emqx_auth_mnesia
```

For EMQ X we only support authentication by clientid, so the default authentication needs to be overridden:
```shell
# emqx_auth_mnesia.conf

## Password hash.
auth.mnesia.password_hash = sha256

## Auth as username or auth as clientid.
auth.mnesia.as = clientid
```

### Service API

```kotlin
class TestClientRegistration(private val clientService: ClientService) {

    private val clientData = ClientData(
        clientId = "testClientId",
        password = "password"
    )
    
    fun register() {
        
        // Registers the client and generates an acl rule with publish and subscribe permission for the topic clientId/#
        // Can consume a custom acl rule: AclRule(login: String, topic: String, action: String, allow: Boolean)
        // Where login is some clientId and action is "pub"|"sub"|"pubsub"
        // Returns RegistrationResult(success: Boolean, message: String?)
        clientService.registerClient(clientData)
    }
    
    fun unregister() {
        
        // Returns RegistrationResult(success: Boolean, message: String?)
        clientService.unregisterClient("testClientId") 
    }
}
```