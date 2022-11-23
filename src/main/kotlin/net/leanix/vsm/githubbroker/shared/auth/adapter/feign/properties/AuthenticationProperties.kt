package net.leanix.vsm.githubbroker.shared.auth.adapter.feign.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "leanix.vsm.auth")
data class AuthenticationProperties(
    val clientId: String,
    val clientSecret: String
)
