package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Language
import net.leanix.vsm.githubbroker.connector.domain.LanguageProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class LanguageService(private val languageProvider: LanguageProvider) {

    private val logger = LoggerFactory.getLogger(LanguageService::class.java)

    @Async
    fun save(language: Language, assignment: Assignment) {
        kotlin.runCatching {
            languageProvider.save(language, assignment)
        }.onFailure {
            logger.error("Failed save service", it)
        }
    }
}
