package net.leanix.vsm.githubbroker.connector.scheduler

import net.leanix.vsm.githubbroker.connector.application.AssignmentService
import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.CommandEventAction
import net.leanix.vsm.githubbroker.connector.domain.CommandProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleRepositories(
    private val assignmentService: AssignmentService,
    private val repositoriesService: RepositoriesService,
    private val commandProvider: CommandProvider
) {
    private val logger: Logger = LoggerFactory.getLogger(ScheduleRepositories::class.java)

    @Scheduled(cron = "\${leanix.vsm.schedule:0 0 4 * * *}")
    fun getAllRepositories() {
        logger.info("Started schedule")
        getAssignments()?.forEach { assignment ->
            kotlin.runCatching {
                repositoriesService.getAllRepositories(assignment)
                commandProvider.sendCommand(assignment, CommandEventAction.FINISHED)
            }.onFailure {
                commandProvider.sendCommand(assignment, CommandEventAction.FAILED)
                logger.error("Schedule failed", it)
            }
        }
    }

    private fun getAssignments(): List<Assignment>? {
        return kotlin.runCatching {
            return assignmentService.getAssignments()
        }.getOrElse {
            logger.error("Failed to get initial state. No assignment found for this workspace id")
            null
        }
    }
}
