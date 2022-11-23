package net.leanix.vsm.githubbroker.connector.runner

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock

@SpringBootTest
@AutoConfigureWireMock(port = 6666)
class InitialStateTest {

    @Test
    fun `it should get the assignment`() {
        WireMock.verify(
            1,
            getRequestedFor(urlEqualTo("/assignment/github-on-prem-repository-connector/git-on-prem-config"))
        )
    }
}
