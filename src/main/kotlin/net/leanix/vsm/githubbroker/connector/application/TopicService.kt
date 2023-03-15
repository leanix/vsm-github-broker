package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Topic
import net.leanix.vsm.githubbroker.connector.domain.TopicProvider
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class TopicService(private val topicProvider: TopicProvider) {

    private val logger = LoggerFactory.getLogger(TopicService::class.java)

    @Async
    fun save(topic: Topic, assignment: Assignment) {
        kotlin.runCatching {
            topicProvider.save(topic, assignment)
        }.onFailure {
            logger.error("Failed save topic", it)
        }
    }
}
