package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.AdminLog
import net.leanix.vsm.githubbroker.logs.domain.LogLevel
import net.leanix.vsm.githubbroker.shared.exception.VsmException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RepositoriesService(
    private val assignmentService: AssignmentService,
    private val repositoryService: RepositoryService,
    private val githubRepositoryProvider: GithubRepositoryProvider,
    private val loggingService: LoggingService
) {

    private val logger = LoggerFactory.getLogger(RepositoriesService::class.java)

    fun getAllRepositories() {
        assignmentService
            .get()
            .map { getRepositoriesPaginated(it) }
            .onFailure {
                // TODO send status log here?
                logger.error("Failed fetch repos", it)
            }
    }

    private fun getRepositoriesPaginated(assignment: Assignment) {
        var cursor: String? = null
        var totalRepos = 0
        do {
            val repos = githubRepositoryProvider
                .getAllRepositories(assignment.organizationName, cursor)
                .getOrElse {
                    handleExceptions(it, assignment)
                    throw it
                }
            val repositories = repos.repositories
            totalRepos += repositories.size
            repositories
                .forEach { repositoryService.save(it, assignment) }
            cursor = repos.cursor
        } while (repos.hasNextPage)
    }

    private fun handleExceptions(exception: Throwable, assignment: Assignment) {
        when (exception) {
            is VsmException.NoRepositoriesFound -> {
                logFailedMessages(
                    "Zero repositories found in ${assignment.organizationName} GitHub organisation. Hint: In case organisation is valid, check if the inclusion list has at least one valid repository name.",
                    assignment
                )
            }
        }
    }

    private fun logFailedMessages(message: String, assignment: Assignment) {
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.ERROR.toString(),
                level = LogLevel.ERROR,
                message = message
            )
        )
    }
}
