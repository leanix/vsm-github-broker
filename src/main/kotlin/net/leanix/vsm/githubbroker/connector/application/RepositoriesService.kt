package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.EventType.STATE
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.shared.exception.VsmException
import org.springframework.stereotype.Service

@Service
class RepositoriesService(
    private val repositoryService: RepositoryService,
    private val githubRepositoryProvider: GithubRepositoryProvider,

) : BaseConnectorService() {

    fun getAllRepositories(assignment: Assignment) {
        logInfoStatus(runId = assignment.runId, status = LogStatus.IN_PROGRESS)
        getRepositoriesPaginated(assignment)
        logInfoStatus(runId = assignment.runId, status = LogStatus.SUCCESSFUL)
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
                .forEach { repositoryService.save(it, assignment, STATE) }
            cursor = repos.cursor
        } while (repos.hasNextPage)
        logInfoMessages("vsm.repos.total", arrayOf(totalRepos), assignment)
    }

    private fun handleExceptions(exception: Throwable, assignment: Assignment) {
        when (exception) {
            is VsmException.NoRepositoriesFound -> {
                logFailedMessages("vsm.repos.not_found", arrayOf(assignment.organizationName), assignment)
            }
        }
    }
}
