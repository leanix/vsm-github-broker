package net.leanix.vsm.githubbroker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GithubBrokerApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<GithubBrokerApplication>(*args)
}
