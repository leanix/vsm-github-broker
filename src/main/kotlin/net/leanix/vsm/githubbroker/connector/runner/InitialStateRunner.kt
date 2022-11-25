package net.leanix.vsm.githubbroker.connector.runner

import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner(private val repositoriesService: RepositoriesService) : ApplicationRunner {
    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)
    override fun run(args: ApplicationArguments?) {
        kotlin.runCatching {
            logger.info("Started get initial state")
            repositoriesService.getAllRepositories()
        }.onFailure {
            logger.error("Failed to get initial state")
        }
    }
}
