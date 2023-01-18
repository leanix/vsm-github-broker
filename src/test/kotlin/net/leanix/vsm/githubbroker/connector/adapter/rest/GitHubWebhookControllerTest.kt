package net.leanix.vsm.githubbroker.connector.adapter.rest

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest
@AutoConfigureWireMock(port = 6666)
class GitHubWebhookControllerTest
