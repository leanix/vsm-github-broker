package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.adapter.feign.GitHubClient
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.Config
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.GitHubWebhookRequest
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GitHubWebhookService(
    private val gitHubClient: GitHubClient,
    private val vsmProperties: VsmProperties
) {

    private val logger = LoggerFactory.getLogger(GitHubWebhookService::class.java)

    fun registerWebhook(orgName: String) {
        // todo add tests
        runCatching {
            val hooks = gitHubClient.getHooks(orgName)
            hooks.forEach {
                logger.info("Deleting hook to ensure unique change events: ${it.id}")
                gitHubClient.deleteHook(orgName, it.id)
            }
        }.onFailure {
            logger.info("No hooks identified. Attempting to create a new one")
        }

        logger.info("registering webhooks")

        kotlin.runCatching {
            gitHubClient.createHook(
                orgName,
                GitHubWebhookRequest(
                    events = listOf("push"),
                    config = Config(
                        url = vsmProperties.githubUrl,
                        contentType = "json"
                    )
                )
            )
        }.onFailure {
            logger.warn(
                "Failed to register webhook. Please check the logs for more details. " +
                    "Until then real time updates are not available. Error: ${it.message}"
            )
        }.onSuccess {
            logger.info("Successfully registered webhook. Real time updates are now available. Hook id: ${it.id}")
        }
    }
}
