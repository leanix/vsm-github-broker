package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.AssignmentResponse
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.FeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "assignmentClient",
    url = "\${leanix.vsm.base-url}",
    configuration = [FeignClientConfiguration::class]
)
interface AssignmentClient {

    @GetMapping("/broker/assignment")
    fun getAssignment(
        @RequestParam("integrationName") integrationName: String,
        @RequestParam("configurationName") configurationName: String
    ): AssignmentResponse
}
