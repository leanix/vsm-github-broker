package net.leanix.vsm.githubbroker.connector.runner

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.awaitility.kotlin.await
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest
@AutoConfigureWireMock(port = 6666)
class InitialStateTest {

    @Test
    fun `it should get the assignment`() {
        await.untilAsserted {
            WireMock.verify(
                1,
                getRequestedFor(
                    urlEqualTo(
                        "/broker/assignment?" +
                            "integrationName=github-enterprise-repository-connector" +
                            "&configurationName=git-on-prem-config"
                    )
                )
            )

            WireMock.verify(
                1,
                postRequestedFor(urlEqualTo("/api/graphql"))
                    .withRequestBody(WireMock.containing("AllRepoQuery"))

            )
            WireMock.verify(
                2,
                postRequestedFor(urlEqualTo("/api/graphql"))
                    .withRequestBody(WireMock.containing("GetReposPullRequestsQuery"))

            )
            WireMock.verify(2, postRequestedFor(urlEqualTo("/services")))
            WireMock.verify(3, postRequestedFor(urlEqualTo("/languages")))
            WireMock.verify(2, postRequestedFor(urlEqualTo("/topics")))
            WireMock.verify(1, postRequestedFor(urlEqualTo("/logs/admin")))
        }
    }
}
