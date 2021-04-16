package de.smartsquare.starter.mqttadmin

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

abstract class EmqxInfrastructure {

    companion object {
        private val emqx = KGenericContainer(DockerImageName.parse("emqx/emqx:4.2.7"))
            .withEnv("EMQX_LOADED_PLUGINS", "emqx_management,emqx_dashboard,emqx_auth_username,emqx_auth_mnesia")
            .withEnv("EMQX_AUTH__MNESIA__AS", "username")
            .withEnv("EMQX_ALLOW_ANONYMOUS", "false")
            .withClasspathResourceMapping(
                "acl.conf",
                "/opt/emqx/etc/acl.conf",
                BindMode.READ_ONLY
            )
            .withExposedPorts(1883, 8081, 18083)
            .waitingFor(Wait.forLogMessage(".*is running now!.*", 1))

        @JvmStatic
        @DynamicPropertySource
        fun emqxApiProperties(registry: DynamicPropertyRegistry) {
            registry.add("emqx.api.port") { emqx.getMappedPort(18083) }
            registry.add("emqx.api.host") { "localhost" }
            registry.add("emqx.api.username") { "admin" }
            registry.add("emqx.api.password") { "public" }
        }

        init {
            emqx.start()
        }
    }
}