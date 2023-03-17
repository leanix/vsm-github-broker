package net.leanix.vsm.githubbroker.connector.application

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import net.leanix.vsm.githubbroker.connector.adapter.feign.GitHubClient
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.Config
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.EventType
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.GitHubWebhookRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.WebhookEventType
import net.leanix.vsm.githubbroker.connector.domain.WebhookParseProvider
import net.leanix.vsm.githubbroker.shared.exception.VsmException
import net.leanix.vsm.githubbroker.shared.exception.VsmException.WebhookEventValidationFailed
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.UUID

@ConditionalOnProperty(
    prefix = "leanix.vsm.webhook",
    value = ["enabled"],
    havingValue = "true",
    matchIfMissing = false
)
@Service
class GitHubWebhookService(
    private val vsmProperties: VsmProperties,
    private val gitHubClient: GitHubClient,
    private val assignmentService: AssignmentService,
    private val webhookParseProvider: WebhookParseProvider,
    private val repositoryService: RepositoryService
) : WebhookService, BaseConnectorService() {

    private val logger = LoggerFactory.getLogger(GitHubWebhookService::class.java)
    private val mapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    override fun registerWebhook(orgName: String) {
        logger.info("Initializing webhooks registration steps. orgName: $orgName")

        runCatching {
            cleanHooks(orgName)
        }.onFailure {
            if (it.message?.contains("404") == true) {
                logger.info("No hooks identified. Attempting to create a new one. orgName: $orgName")
            } else {
                logger.error("Failed to register webhooks for $orgName. Error: ${it.message}")
                throw VsmException.WebhookRegistrationFailed(
                    "Failed to initialise webhooks state for $orgName." +
                        " Hint: Make sure PAT is valid. Error: ${it.message}"
                )
            }
        }

        logger.info("registering webhooks")

        kotlin.runCatching {
            createHook(orgName)
        }.onFailure {
            logger.warn(
                "Failed to register webhook. Please check the logs for more details. " +
                    "Until then real time updates are not available. Error: ${it.message}"
            )
            throw VsmException.WebhookRegistrationFailed(
                "Failed to register webhook. Real time updates are unavailable. " +
                    "Hint: Make sure PAT is valid. Hint: Make sure PAT has necessary scopes (admin:org_hook)." +
                    " Error: ${it.message}"
            )
        }.onSuccess {
            logger.info("Successfully registered webhook. Real time updates are now available.")
        }
    }

    private fun createHook(orgName: String) {
        val hook = gitHubClient.createHook(
            orgName,
            GitHubWebhookRequest(
                events = listOf("push", "pull_request", "repository"),
                config = Config(
                    url = "${vsmProperties.brokerUrl}/github/${vsmProperties.apiToken}/webhook",
                    contentType = "json"
                )
            )
        )

        logger.info("Successfully created hook. hook: ${hook.id}")
    }

    private fun cleanHooks(orgName: String) {
        val hooks = gitHubClient.getHooks(orgName)
        hooks
            .filter { it.config.url.contains(vsmProperties.apiToken) }
            .forEach {
                logger.info("Deleting hook to ensure unique change events: ${it.id}")
                kotlin.runCatching {
                    gitHubClient.deleteHook(orgName, it.id)
                }.onFailure { e ->
                    logger.info("Failed to delete hook. Hook Id: ${it.id}. Error: ${e.message}")
                }
            }
    }

    @Async
    override fun consumeWebhookEvent(eventType: WebhookEventType, apiToken: String, payload: String) {
        logger.info("new webhook event received: $eventType")

        runCatching {
            val assignmentList = assignmentService.getAssignments()
            assignmentList.forEach { assignment ->
                validateRequest(apiToken, payload, assignment)
                    .onSuccess {
                        val repository = webhookParseProvider.parsePayload(eventType, payload, assignment)
                        repositoryService.save(repository, assignment, EventType.CHANGE)
                        logInfoMessages("vsm.repos.imported", emptyArray(), assignment)
                    }
                    .onFailure {
                        logFailedStatus(
                            runId = assignment.runId,
                            message = it.message
                        )
                    }
            }
        }.onFailure {
            logFailedStatus(
                runId = UUID.randomUUID(),
                message = it.message
            )
        }
    }

    private fun validateRequest(apiToken: String, payload: String, assignment: Assignment): Result<Unit> {
        val organization = mapper.readValue<JsonNode>(payload).get("organization").get("login").asText()
        return if (apiToken == vsmProperties.apiToken && assignment.organizationName == organization) {
            logger.info("api token and organization are valid")
            Result.success(Unit)
        } else {
            val message = "invalid api token: $apiToken or organization: $organization"
            Result.failure(WebhookEventValidationFailed(message))
        }
    }
}
