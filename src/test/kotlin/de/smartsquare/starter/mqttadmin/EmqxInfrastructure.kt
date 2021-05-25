package de.smartsquare.starter.mqttadmin

import org.slf4j.LoggerFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName

abstract class EmqxInfrastructure {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        private val logConsumer get() = Slf4jLogConsumer(logger).withSeparateOutputStreams()

        private val emqx = KGenericContainer(DockerImageName.parse("emqx/emqx:4.3.1"))
            .withEnv("EMQX_LOADED_PLUGINS", "emqx_management,emqx_auth_mnesia")
            .withEnv("EMQX_ALLOW_ANONYMOUS", "false")
            .withEnv("WAIT_FOR_ERLANG", "60")
            .withExposedPorts(1883, 8081, 18083)
            .waitingFor(Wait.forLogMessage(".*is running now!.*", 1))
            .withLogConsumer(logConsumer.withPrefix("emqx"))

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
