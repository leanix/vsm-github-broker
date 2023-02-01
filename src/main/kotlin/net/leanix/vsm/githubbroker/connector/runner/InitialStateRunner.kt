package net.leanix.vsm.githubbroker.connector.runner

import net.leanix.vsm.githubbroker.connector.application.AssignmentService
import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import net.leanix.vsm.githubbroker.connector.application.WebhookService
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
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
    matchIfMissing = true
)
@Component
class InitialStateRunner(
    private val assignmentService: AssignmentService,
    private val repositoriesService: RepositoriesService,
    private val webhookService: WebhookService,
    private val loggingService: LoggingService
) : ApplicationRunner {
    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)
    override fun run(args: ApplicationArguments?) {
        logger.info("Started get initial state")
        getAssignment()?.let {
            kotlin.runCatching {
                repositoriesService.getAllRepositories(it)
                logger.info("Initializing webhooks registration steps")
                webhookService.registerWebhook(it.organizationName)
            }.onFailure { e ->
                logger.error("Failed to get initial state", e)
                loggingService.sendStatusLog(
                    StatusLog(
                        it.runId,
                        LogStatus.FAILED,
                        "Failed to get initial state. Error: ${e.message}"
                    )
                )
            }
        }
    }
    private fun getAssignment(): Assignment? {
        kotlin.runCatching {
            return assignmentService.getAssignment()
        }.onFailure {
            logger.error("Failed to get initial state. Assignment not found")
        }
        return null
    }
}
