package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.AdminLog
import net.leanix.vsm.githubbroker.logs.domain.LogLevel
import net.leanix.vsm.githubbroker.shared.exception.VsmException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.stereotype.Service
import java.util.Locale

@Service
class RepositoriesService(
    private val repositoryService: RepositoryService,
    private val githubRepositoryProvider: GithubRepositoryProvider,
    private val loggingService: LoggingService,
    private val messageSource: MessageSource
) {

    private val logger = LoggerFactory.getLogger(RepositoriesService::class.java)

    fun getAllRepositories(assignment: Assignment) {
        getRepositoriesPaginated(assignment)
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
        logInfoMessages(
            messageSource.getMessage(
                "vsm.repos.total",
                arrayOf(totalRepos),
                Locale.ENGLISH
            ),
            assignment
        )
    }

    private fun handleExceptions(exception: Throwable, assignment: Assignment) {
        when (exception) {
            is VsmException.NoRepositoriesFound -> {
                logFailedMessages(
                    messageSource.getMessage(
                        "vsm.repos.not_found",
                        arrayOf(assignment.organizationName),
                        Locale.ENGLISH
                    ),
                    assignment
                )
            }
        }
    }

    private fun logInfoMessages(message: String, assignment: Assignment) {
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.INFO.toString(),
                level = LogLevel.INFO,
                message = message
            )
        )
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
