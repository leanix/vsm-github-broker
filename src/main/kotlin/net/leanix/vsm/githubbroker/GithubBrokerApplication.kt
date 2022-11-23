package net.leanix.vsm.githubbroker

import net.leanix.vsm.githubbroker.connector.properties.VsmProperties
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.properties.AuthenticationProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(VsmProperties::class, AuthenticationProperties::class)
class GithubBrokerApplication

fun main(vararg args: String) {
    runApplication<GithubBrokerApplication>(*args)
}
