package net.leanix.vsm.githubbroker.connector.scheduler

import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleRepositories(private val repositoriesService: RepositoriesService) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduleRepositories::class.java)

    @Scheduled(cron = "\${leanix.vsm.schedule:0 0 3 * * *}")
    fun getAllRepositories() {
        kotlin.runCatching {
            logger.info("Started schedule")
            repositoriesService.getAllRepositories()
        }.onFailure {
            logger.error("Schedule failed", it)
        }
    }
}
