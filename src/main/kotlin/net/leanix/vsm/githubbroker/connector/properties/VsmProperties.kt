package net.leanix.vsm.githubbroker.connector.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "leanix.vsm.connector")
data class VsmProperties(
    val apiToken: String,
    val userToken: String,
    val configName: String
)