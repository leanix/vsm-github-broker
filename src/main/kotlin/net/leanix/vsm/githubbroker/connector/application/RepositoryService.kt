package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.EventType
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.RepositoryProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RepositoryService(
    private val repositoryProvider: RepositoryProvider,
    private val doraService: DoraService
) {

    private val logger = LoggerFactory.getLogger(RepositoryService::class.java)

    fun save(repository: Repository, assignment: Assignment, eventType: EventType) {
        kotlin.runCatching {
            repositoryProvider.save(repository, assignment, eventType)
        }.onFailure {
            logger.error("Failed save service", it)
        }

        doraService.generateDoraEvents(repository, assignment)
    }
}
