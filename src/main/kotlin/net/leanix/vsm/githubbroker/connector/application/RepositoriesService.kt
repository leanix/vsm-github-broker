package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
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
        do {
            val repos = githubRepositoryProvider
                .getAllRepositories(assignment.organizationName, cursor)
                .getOrElse {
                    loggingService.sendStatusLog(
                        StatusLog(
                            runId = assignment.runId,
                            status = LogStatus.FAILED,
                            message = it.message
                        )
                    )
                    throw it
                }

            repos.repositories
                .forEach { repositoryService.save(it, assignment) }
            cursor = repos.cursor
        } while (repos.hasNextPage)
    }
}
