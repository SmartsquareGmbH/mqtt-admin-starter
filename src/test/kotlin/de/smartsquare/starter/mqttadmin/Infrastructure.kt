package de.smartsquare.starter.mqttadmin

import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.builder.ImageFromDockerfile

abstract class Infrastructure {

    companion object {
        val emqx = KGenericContainer(
            ImageFromDockerfile("emqx-e2e-test", false)
                .withFileFromClasspath("emqx_auth_mnesia.conf", "emqx_auth_mnesia.conf")
                .withFileFromClasspath("Dockerfile", "Dockerfile")
        ).waitingFor(Wait.forLogMessage(".*is running now!.*", 1))

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
