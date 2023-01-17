package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.RepositoryProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class RepositoryService(
    private val repositoryProvider: RepositoryProvider,
    private val languageService: LanguageService,
    private val topicService: TopicService
) {

    private val logger = LoggerFactory.getLogger(RepositoryService::class.java)

    @Async
    fun save(repository: Repository, assignment: Assignment) {
        kotlin.runCatching {
            repositoryProvider.save(repository, assignment)
        }.onFailure {
            logger.error("Failed save service", it)
        }

        kotlin.runCatching {
            repository.languages?.forEach { language -> languageService.save(language, assignment) }
        }.onFailure {
            logger.error("Failed save languages", it)
        }

        kotlin.runCatching {
            repository.topics?.forEach { topic -> topicService.save(topic, assignment) }
        }.onFailure {
            logger.error("Failed save topics", it)
        }
    }
}
