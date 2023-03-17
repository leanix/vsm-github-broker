package net.leanix.vsm.githubbroker.shared.properties

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.util.*
import jakarta.annotation.PostConstruct

@Configuration
class GradleProperties {

    private val logger: Logger = LoggerFactory.getLogger(GradleProperties::class.java)

    companion object {
        lateinit var GITHUB_ENTERPRISE_VERSION: String
            private set
    }

    @PostConstruct
    fun loadVersion() {
        try {
            val gradleProperties = Properties()
            gradleProperties.load(this::class.java.getResourceAsStream("/gradle.properties"))
            GITHUB_ENTERPRISE_VERSION = gradleProperties.getProperty("version")
            logger.info("Running GitHub enterprise on version: $GITHUB_ENTERPRISE_VERSION")
        } catch (e: RuntimeException) {
            GITHUB_ENTERPRISE_VERSION = "unknown"
            logger.error("Unable to load GitHub enterprise version")
        }
    }
}
