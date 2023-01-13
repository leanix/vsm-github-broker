package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.TopicRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Topic
import net.leanix.vsm.githubbroker.connector.domain.TopicProvider
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE
import org.springframework.stereotype.Component

@Component
class FeignTopicProvider(private val topicClient: TopicClient) : TopicProvider {

    override fun save(topic: Topic, assignment: Assignment) {
        val topicToBeSaved = TopicRequest(
            id = topic.id,
            runId = assignment.runId,
            source = GITHUB_ENTERPRISE,
            name = topic.name,
            organizationName = assignment.organizationName
        )

        topicClient.saveTopic(topicToBeSaved)
    }
}
