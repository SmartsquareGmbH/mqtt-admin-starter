package de.smartsquare.starter.mqttadmin

import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.slf4j.LoggerFactory
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.DockerImageName
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class EmqxExtension : BeforeAllCallback {

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
    }

    private val lock = ReentrantLock()

    override fun beforeAll(context: ExtensionContext) {
        val store = context.root.getStore(ExtensionContext.Namespace.GLOBAL)

        lock.withLock {
            val emqxStarted = store.getOrDefault("emqx", Boolean::class.java, false)

            if (!emqxStarted) {
                emqx.start()

                store.put("emqx", true)
            }
        }

        System.setProperty("emqx.api.host", emqx.host)
        System.setProperty("emqx.api.port", emqx.getMappedPort(18083).toString())
        System.setProperty("emqx.api.username", "admin")
        System.setProperty("emqx.api.password", "public")
    }
}
