package net.leanix.vsm.githubbroker

import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(VsmProperties::class)
class GithubBrokerApplication

fun main() {
    runApplication<GithubBrokerApplication>()
}
