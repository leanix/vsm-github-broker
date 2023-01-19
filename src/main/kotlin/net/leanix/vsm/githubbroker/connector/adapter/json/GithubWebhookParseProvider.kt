package net.leanix.vsm.githubbroker.connector.adapter.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.WebhookEventType
import net.leanix.vsm.githubbroker.connector.domain.WebhookParseProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class GithubWebhookParseProvider : WebhookParseProvider {

    private val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    private val logger = LoggerFactory.getLogger(GithubWebhookParseProvider::class.java)

    override fun parsePayload(
        eventType: WebhookEventType,
        payload: String,
        assignment: Assignment
    ): Result<Repository> {
        return when(eventType) {
            WebhookEventType.REPOSITORY -> parseRepositoryPayload(payload, assignment)
            WebhookEventType.PULL_REQUEST -> parsePullRequestPayload(payload, assignment)
        }
    }

    private fun parseRepositoryPayload(
        payload: String,
        assignment: Assignment
    ) : Result<Repository> {
        TODO()
    }

    private fun parsePullRequestPayload(
        payload: String,
        assignment: Assignment
    ) : Result<Repository> {
        TODO()
    }
}