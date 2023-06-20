package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.AssignmentResponse
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.CommandRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.LanguageRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.ServiceRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.TopicRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.githubbroker.shared.Constants.EVENT_TYPE_HEADER
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.MtmFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "servicesClient",
    url = "\${leanix.vsm.event-broker.base-url}",
    configuration = [MtmFeignClientConfiguration::class]
)
interface VsmClient {

    @PostMapping("/services")
    fun saveService(
        @RequestHeader(name = EVENT_TYPE_HEADER) eventType: String,
        @RequestBody serviceRequest: ServiceRequest,
    )

    @PostMapping("/commands")
    fun sendCommand(
        @RequestBody commandRequest: CommandRequest,
    )

    @PostMapping("/languages")
    fun saveLanguage(languageRequest: LanguageRequest)

    @PostMapping("/topics")
    fun saveTopic(topicRequest: TopicRequest)

    @PostMapping("/dora")
    fun saveDora(doraRequest: DoraRequest)

    @GetMapping("/assignments")
    fun getAssignments(
        @RequestParam("integrationName") integrationName: String,
        @RequestParam("configSetName") configSetName: String
    ): AssignmentResponse

    @GetMapping("/health/heartbeat")
    fun heartbeat(@RequestParam("runId") runId: String): String

    @PutMapping("/run/status")
    fun updateRunState(
        @RequestParam("runId") runId: String,
        @RequestBody runState: UpdateRunStateRequest,
    )
}
