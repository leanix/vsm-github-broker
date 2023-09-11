package net.leanix.vsm.githubbroker.connector.adapter.rest

import com.github.tomakehurst.wiremock.client.WireMock
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.awaitility.kotlin.await
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import java.util.*

@SpringBootTest
@AutoConfigureWireMock(port = 6666)
class GitHubWebhookControllerTest {

    @Autowired
    private lateinit var context: WebApplicationContext

    internal lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .build()
        WireMock.resetAllRequests()
        AssignmentCache.deleteAll()
        AssignmentCache.addAll(
            listOf(
                Assignment(
                    runId = UUID.fromString("f1abfae5-f144-47e1-9cda-3eaa94e5286d"),
                    configurationId = UUID.fromString("a7a74e83-dde9-48a0-8b0d-c74f954671fb"),
                    workspaceId = UUID.fromString("38718fc9-d106-47a5-a25c-e4e595c8c2d4"),
                    organizationName = "super-repo"
                ),
                Assignment(
                    runId = UUID.fromString("f1abfae5-f144-47e1-9cda-3eaa94e5286d"),
                    configurationId = UUID.fromString("a7a74e83-dde9-48a0-8b0d-c74f954671fb"),
                    workspaceId = UUID.fromString("38718fc9-d106-47a5-a25c-e4e595c8c2d4"),
                    organizationName = "super-duper-repo"
                )
            )
        )
    }

    @Nested
    @DisplayName("Validate webhook Tests")
    inner class ValidateWebhookTests {

        @Test
        fun `it should not save service when token is invalid`() {
            val request = this::class.java.classLoader.getResource(
                "requests/github-event-repository-created-payload.json"
            )!!.readText()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/wrong-token/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
                    .header("X-Github-Event", "repository")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            Thread.sleep(2000)
            WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/services")))
            WireMock.verify(2, WireMock.postRequestedFor(WireMock.urlEqualTo("/log/connector-status")))
        }

        @Test
        fun `it should not save service when org is not supported invalid`() {
            val apiToken = "api-token"
            val organization = "not-supported-repo"
            val request = this::class.java.classLoader.getResource(
                "requests/github-event-not-support-org-payload.json"
            )!!.readText()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/$apiToken/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
                    .header("X-Github-Event", "repository")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            Thread.sleep(3000)
            WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/services")))
            WireMock.verify(
                2,
                WireMock.postRequestedFor(WireMock.urlEqualTo("/log/connector-status"))
                    .withRequestBody(WireMock.containing("invalid api token: $apiToken or organization: $organization"))
            )
        }
    }

    @Nested
    @DisplayName("Repository Event Tests")
    inner class RepositoryEventTests {

        @Test
        fun `it should receive an repository event with action created with success`() {
            val request = this::class.java.classLoader.getResource(
                "requests/github-event-repository-created-payload.json"
            )!!.readText()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/api-token/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
                    .header("X-Github-Event", "repository")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            await.untilAsserted {
                WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/services")))
                WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/logs/admin")))
            }
        }

        @Test
        fun `it should receive an repository event with action edited with success`() {
            val request = this::class.java.classLoader.getResource(
                "requests/github-event-repository-edited-payload.json"
            )!!.readText()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/api-token/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
                    .header("X-Github-Event", "repository")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            await.untilAsserted {
                WireMock.verify(
                    1,
                    WireMock.postRequestedFor(WireMock.urlEqualTo("/services"))
                        .withRequestBody(WireMock.containing("\"description\":\"add new description\""))
                )
                WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/languages")))
                WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/topics")))
                WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/logs/admin")))
            }
        }
    }

    @Nested
    @DisplayName("PullRequest Event Tests")
    inner class PullRequestEventTests {

        @Test
        fun `it should receive an pull request event with action closed with success`() {
            val request = this::class.java.classLoader.getResource(
                "requests/github-event-pullrequest-closed-payload.json"
            )!!.readText()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/api-token/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
                    .header("X-Github-Event", "pull_request")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            await.untilAsserted {
                WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/services")))
                WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/logs/admin")))
            }
        }

        @Test
        fun `it should receive an pull request event with action closed and not merged with success`() {
            val request = this::class.java.classLoader.getResource(
                "requests/github-event-pullrequest-closed-not-merged-payload.json"
            )!!.readText()

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/api-token/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content(request)
                    .header("X-Github-Event", "pull_request")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            await.untilAsserted {
                WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/services")))
            }
        }
    }
}
