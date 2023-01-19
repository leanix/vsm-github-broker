package net.leanix.vsm.githubbroker.connector.adapter.rest

import com.github.tomakehurst.wiremock.client.WireMock
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
    }

    @Nested
    @DisplayName("Validate webhook Tests")
    inner class ValidateWebhookTests {

        @Test
        fun `it should not save service when token is invalid`() {

            mockMvc.perform(
                MockMvcRequestBuilders.post("/github/wrong-token/webhook")
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .content("{}")
                    .header("X-Github-Event", "repository")
            )
                .andExpect(MockMvcResultMatchers.status().isAccepted)

            WireMock.verify(0, WireMock.postRequestedFor(WireMock.urlEqualTo("/services")))
            WireMock.verify(1, WireMock.postRequestedFor(WireMock.urlEqualTo("/log/connector-status"))
                .withRequestBody(WireMock.containing("invalid api token")))
        }
    }
}
