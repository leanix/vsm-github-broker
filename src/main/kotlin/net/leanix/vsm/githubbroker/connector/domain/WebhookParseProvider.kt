package net.leanix.vsm.githubbroker.connector.domain

interface WebhookParseProvider {

    fun parsePayload(eventType: WebhookEventType, payload: String, assignment: Assignment): Repository
}
