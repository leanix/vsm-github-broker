package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.WebhookEventType

interface WebhookService {

    fun registerWebhook(orgName: String)

    fun consumeWebhookEvent(eventType: WebhookEventType, apiToken: String, payload: String)
}
