package net.leanix.vsm.githubbroker.connector.scheduler

import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import net.leanix.vsm.githubbroker.connector.domain.CommandEventAction
import net.leanix.vsm.githubbroker.connector.domain.CommandProvider
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleRepositories(
    private val repositoriesService: RepositoriesService,
    private val commandProvider: CommandProvider,
) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduleRepositories::class.java)

    @Scheduled(cron = "\${leanix.vsm.schedule:0 0 4 * * *}")
    fun getAllRepositories() {
        logger.info("Started schedule")
        runCatching {
            AssignmentCache.getAll().values.forEach { assignment ->
                repositoriesService.getAllRepositories(assignment)
            }
        }.onSuccess {
            AssignmentCache.getAll().firstNotNullOf {
                commandProvider.sendCommand(it.value, CommandEventAction.FINISHED)
            }
        }.onFailure { error ->
            AssignmentCache.getAll().firstNotNullOf {
                commandProvider.sendCommand(it.value, CommandEventAction.FAILED)
            }
            logger.error("Schedule failed", error)
        }
    }
}
