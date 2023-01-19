package net.leanix.vsm.githubbroker.connector.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.leanix.vsm.githubbroker.connector.adapter.feign.GitHubClient
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.Config
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.GitHubWebhookResponse
import net.leanix.vsm.githubbroker.connector.domain.WebhookParseProvider
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GitHubWebhookServiceTest {

    private val gitHubClientMock = mockk<GitHubClient>()
    private val vsmPropertiesMock = mockk<VsmProperties>()
    private val assignmentService = mockk<AssignmentService>()
    private val webhookParseProvider = mockk<WebhookParseProvider>()
    private val repositoryService  = mockk<RepositoryService>()

    private val service = GitHubWebhookService(
        vsmPropertiesMock, gitHubClientMock, assignmentService, webhookParseProvider, repositoryService
    )

    @BeforeEach
    fun init() {
        every {
            vsmPropertiesMock.githubUrl
        } returns "https://api.github.com"
    }

    @Test
    fun `When a hook is already present it should delete the hook and create a new hook`() {

        every {
            gitHubClientMock.getHooks("dummy")
        } returns listOf(
            GitHubWebhookResponse(
                id = "12",
                name = "web",
                active = true,
                events = listOf("push"),
                config = Config(
                    url = "https://dummy.com",
                    contentType = "json",
                )
            ),
            GitHubWebhookResponse(
                id = "13",
                name = "web",
                active = true,
                events = listOf("push"),
                config = Config(
                    url = "https://dummy-1.com",
                    contentType = "json",
                )
            )

        )

        every { gitHubClientMock.deleteHook("dummy", "12") } returns Unit
        every { gitHubClientMock.deleteHook("dummy", "13") } returns Unit

        every { gitHubClientMock.createHook("dummy", any()) } returns GitHubWebhookResponse(
            id = "13",
            name = "web",
            active = true,
            events = listOf("push"),
            config = Config(
                url = "https://dummy.com",
                contentType = "json",
            )
        )

        service.registerWebhook("dummy")

        verify(exactly = 2) { gitHubClientMock.deleteHook(any(), any()) }
        verify(exactly = 1) { gitHubClientMock.createHook(any(), any()) }
    }

    @Test
    fun `When a hook is not present it should not delete the hook but create a new hook`() {
        every {
            gitHubClientMock.getHooks("dummy")
        } throws RuntimeException("Not found[404]")

        every { gitHubClientMock.createHook("dummy", any()) } returns GitHubWebhookResponse(
            id = "13",
            name = "web",
            active = true,
            events = listOf("push"),
            config = Config(
                url = "https://dummy.com",
                contentType = "json",
            )
        )

        service.registerWebhook("dummy")

        verify(exactly = 0) { gitHubClientMock.deleteHook(any(), any()) }
        verify(exactly = 1) { gitHubClientMock.createHook(any(), any()) }
    }
}
