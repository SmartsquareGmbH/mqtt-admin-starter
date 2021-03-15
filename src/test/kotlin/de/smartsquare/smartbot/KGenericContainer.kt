package de.smartsquare.smartbot

import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.ImageFromDockerfile

class KGenericContainer(image: ImageFromDockerfile) : GenericContainer<KGenericContainer>(image)