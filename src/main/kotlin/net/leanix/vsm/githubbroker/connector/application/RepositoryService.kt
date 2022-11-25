package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.RepositoryProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class RepositoryService(private val repositoryProvider: RepositoryProvider) {

    private val logger = LoggerFactory.getLogger(RepositoryService::class.java)

    @Async
    fun save(repository: Repository, assignment: Assignment) {
        kotlin.runCatching {
            repositoryProvider.save(repository, assignment)
        }.onFailure {
            logger.error("Failed save service", it)
        }
    }
}