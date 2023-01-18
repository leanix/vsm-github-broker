package net.leanix.vsm.githubbroker

import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(VsmProperties::class)
@EnableAsync
@EnableScheduling
class GithubBrokerApplication

fun main() {
    runApplication<GithubBrokerApplication>()
}
