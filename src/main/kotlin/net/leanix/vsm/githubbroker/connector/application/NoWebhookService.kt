package net.leanix.vsm.githubbroker.connector.application

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service

@ConditionalOnProperty(
    prefix = "leanix.vsm.webhook",
    value = ["enabled"],
    havingValue = "false",
    matchIfMissing = true
)
@Service
class NoWebhookService : WebhookService {

    private val logger = LoggerFactory.getLogger(NoWebhookService::class.java)

    override fun registerWebhook(orgName: String) {
        logger.info("no webhook")
    }
}
