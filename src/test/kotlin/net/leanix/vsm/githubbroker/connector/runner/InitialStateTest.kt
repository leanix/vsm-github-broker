package net.leanix.vsm.githubbroker.connector.runner

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest(properties = ["application.runner.enabled=true"])
@AutoConfigureWireMock(port = 6666)
class InitialStateTest {

    @Test
    fun `it should get the assignments`() {
        AssignmentCache.deleteAll()
        await.untilAsserted {
            WireMock.verify(
                1,
                getRequestedFor(
                    urlEqualTo(
                        "/assignments?" +
                            "integrationName=github-enterprise-repository-connector&" +
                            "configSetName=mock-config-set"
                    )
                )
            )

            WireMock.verify(
                2,
                postRequestedFor(urlEqualTo("/api/graphql"))
                    .withRequestBody(WireMock.containing("AllRepoQuery"))

            )
            WireMock.verify(
                4,
                postRequestedFor(urlEqualTo("/api/graphql"))
                    .withRequestBody(WireMock.containing("GetReposPullRequestsQuery"))

            )
            WireMock.verify(
                4,
                postRequestedFor(urlEqualTo("/api/graphql"))
                    .withRequestBody(WireMock.containing("getPullRequestCommits"))

            )
            WireMock.verify(4, postRequestedFor(urlEqualTo("/services")))
            WireMock.verify(0, postRequestedFor(urlEqualTo("/languages")))
            WireMock.verify(0, postRequestedFor(urlEqualTo("/topics")))
            WireMock.verify(8, postRequestedFor(urlEqualTo("/logs/admin")))
            WireMock.verify(4, postRequestedFor(urlEqualTo("/dora")))
        }
    }
}
