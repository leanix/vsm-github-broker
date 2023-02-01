package net.leanix.vsm.githubbroker.connector.application

interface WebhookService {

    fun registerWebhook(orgName: String)
}
