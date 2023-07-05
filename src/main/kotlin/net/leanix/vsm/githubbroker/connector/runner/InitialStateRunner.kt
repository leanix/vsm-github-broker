package net.leanix.vsm.githubbroker.connector.runner

import net.leanix.vsm.githubbroker.connector.application.AssignmentService
import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import net.leanix.vsm.githubbroker.connector.application.WebhookService
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.CommandEventAction
import net.leanix.vsm.githubbroker.connector.domain.CommandProvider
import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@ConditionalOnProperty(
    prefix = "application.runner",
    value = ["enabled"],
    havingValue = "true",
    matchIfMissing = true,
)
@Component
class InitialStateRunner(
    private val assignmentService: AssignmentService,
    private val repositoriesService: RepositoriesService,
    private val webhookService: WebhookService,
    private val loggingService: LoggingService,
    private val commandProvider: CommandProvider,
) : ApplicationRunner {
    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)
    override fun run(args: ApplicationArguments?) {
        logger.info("Started get initial state")

        runCatching {
            getAssignments()?.forEach { assignment ->
                repositoriesService.getAllRepositories(assignment)
                logger.info("Initializing webhooks registration steps")
                webhookService.registerWebhook(assignment.organizationName)
            }
        }.onSuccess {
            AssignmentCache.getAll().firstNotNullOf {
                commandProvider.sendCommand(it.value, CommandEventAction.FINISHED)
            }
        }.onFailure { e ->
            logger.error("Failed to get initial state", e)
            AssignmentCache.getAll().firstNotNullOf {
                loggingService.sendStatusLog(
                    StatusLog(
                        it.value.runId,
                        LogStatus.FAILED,
                        "Failed to get initial state. Error: ${e.message}",
                    ),
                )
                commandProvider.sendCommand(it.value, CommandEventAction.FAILED)
            }
        }
    }
    private fun getAssignments(): List<Assignment>? {
        kotlin.runCatching {
            val assignments = assignmentService.getAssignments()
            AssignmentCache.addAll(assignments)
            return assignments
        }.onFailure {
            logger.error("Failed to get initial state. No assignment found for this workspace id")
        }
        return null
    }
}
