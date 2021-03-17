package de.smartsquare.starter.mqttadmin

import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

class KGenericContainer(image: DockerImageName) : GenericContainer<KGenericContainer>(image)
