package net.leanix.vsm.githubbroker.shared.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "leanix.vsm.connector")
data class VsmProperties(
    val apiToken: String,
    val configName: String,
    val githubToken: String,
    val githubUrl: String,
    val githubVersion: String,
    val brokerUrl: String
)
