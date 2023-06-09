package net.leanix.vsm.githubbroker.health

import com.github.tomakehurst.wiremock.client.WireMock
import org.junit.jupiter.api.BeforeEach
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
class HealthEndpointTest {

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

    @Test
    fun `it responds with http status ok`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/actuator/health")
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }
}