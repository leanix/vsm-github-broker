package net.leanix.vsm.githubbroker.connector.runner

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest(properties = ["application.runner.enabled=false"])
@AutoConfigureWireMock(port = 0)
class InitialStateTest {

    @Autowired
    private lateinit var initialStateRunner: InitialStateRunner

    @BeforeEach
    fun setup() {
        WireMock.resetAllRequests()
    }

    @Test
    fun `it should get the assignment`() {
        initialStateRunner.run(null)
        Thread.sleep(2000)
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
        WireMock.verify(1, postRequestedFor(urlEqualTo("/api/graphql")))
        WireMock.verify(3, postRequestedFor(urlEqualTo("/services")))
        WireMock.verify(1, postRequestedFor(urlEqualTo("/logs/admin")))
    }
}
