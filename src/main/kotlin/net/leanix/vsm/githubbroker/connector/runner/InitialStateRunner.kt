package net.leanix.vsm.githubbroker.connector.runner

import net.leanix.vsm.githubbroker.connector.application.AssignmentService
import net.leanix.vsm.githubbroker.connector.application.GitHubWebhookService
import net.leanix.vsm.githubbroker.connector.application.RepositoriesService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner(
    private val assignmentService: AssignmentService,
    private val repositoriesService: RepositoriesService,
    private val gitHubWebhookService: GitHubWebhookService
) : ApplicationRunner {
    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)
    override fun run(args: ApplicationArguments?) {
        kotlin.runCatching {
            val assignment = assignmentService.getAssignment()
            logger.info("Started get initial state")

            repositoriesService.getAllRepositories(assignment)

            gitHubWebhookService.registerWebhook(assignment.organizationName)
        }.onFailure {
            logger.error("Failed to get initial state", it)
        }
    }
}
