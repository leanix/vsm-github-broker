package net.leanix.vsm.githubbroker.connector.adapter.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.leanix.vsm.githubbroker.connector.adapter.json.data.PullRequestPayload
import net.leanix.vsm.githubbroker.connector.adapter.json.data.RepositoryData
import net.leanix.vsm.githubbroker.connector.adapter.json.data.RepositoryPayload
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.WebhookEventType
import net.leanix.vsm.githubbroker.connector.domain.WebhookParseProvider
import net.leanix.vsm.githubbroker.shared.exception.VsmException.ParsePayloadFailed
import net.leanix.vsm.githubbroker.shared.exception.VsmException.WebhookEventOrActionNotSupported
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GithubWebhookParseProvider(
    private val githubRepositoryProvider: GithubRepositoryProvider
) : WebhookParseProvider {

    private val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val logger = LoggerFactory.getLogger(GithubWebhookParseProvider::class.java)

    override fun parsePayload(
        eventType: WebhookEventType,
        payload: String,
        assignment: Assignment
    ): Repository {
        return when (eventType) {
            WebhookEventType.REPOSITORY -> parseRepositoryPayload(payload)
            WebhookEventType.PULL_REQUEST -> parsePullRequestPayload(payload)
        }
    }

    private fun parseRepositoryPayload(
        payload: String
    ): Repository {
        return kotlin.runCatching {
            mapper.readValue<RepositoryPayload>(payload)
        }
            .map { parseRepositoryDataPayload(it.repository) }
            .onFailure {
                logger.error("error parser gh payload", it)
                throw ParsePayloadFailed(it.message)
            }.getOrThrow()
    }

    private fun parsePullRequestPayload(
        payload: String
    ): Repository {
        return kotlin.runCatching {
            mapper.readValue<PullRequestPayload>(payload)
        }
            .map {
                if (it.action == "closed" && it.pullRequest.isMerged()) {
                    parseRepositoryDataPayload(it.repository)
                } else {
                    throw WebhookEventOrActionNotSupported("Pull Request Event not supported")
                }
            }
            .onFailure {
                logger.error("error parser gh payload", it)
                throw it
            }.getOrThrow()
    }

    private fun parseRepositoryDataPayload(repositoryData: RepositoryData): Repository {
        val pair = if (!repositoryData.topics.isNullOrEmpty() || repositoryData.language != null) {
            githubRepositoryProvider.getLanguagesAndTopics(repositoryData.id)
                .onFailure { logger.error("error get language and topics", it) }
                .getOrNull()
        } else {
            Pair(null, null)
        }
        return Repository(
            id = repositoryData.id,
            name = repositoryData.name,
            description = repositoryData.description,
            url = repositoryData.url,
            archived = repositoryData.archived,
            visibility = repositoryData.visibility.lowercase(),
            defaultBranch = repositoryData.defaultBranch,
            languages = pair?.second,
            topics = pair?.first
        )
    }
}
