package net.leanix.vsm.githubbroker.connector.adapter.rest

import net.leanix.vsm.githubbroker.connector.application.WebhookService
import net.leanix.vsm.githubbroker.connector.domain.WebhookEventType
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github")
class GitHubWebhookController(private val webhookService: WebhookService) {

    private val logger = LoggerFactory.getLogger(GitHubWebhookController::class.java)

    @PostMapping("/{apiToken}/webhook")
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun hook(
        @PathVariable apiToken: String,
        @RequestHeader("X-Github-Event") eventType: String,
        @RequestBody payload: String
    ) {
        kotlin.runCatching {
            WebhookEventType.valueOf(eventType.uppercase())
        }.onSuccess {
            webhookService.consumeWebhookEvent(it, apiToken, payload)
        }.onFailure {
            logger.warn("Event Type not supported: $eventType")
        }
    }
}
