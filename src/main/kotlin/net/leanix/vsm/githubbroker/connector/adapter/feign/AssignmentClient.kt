package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.AssignmentResponse
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.FeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable

@FeignClient(
    name = "assignment",
    url = "\${leanix.vsm.base-url}",
    configuration = [FeignClientConfiguration::class]
)
interface AssignmentClient {

    @GetMapping("/assignment/{integrationName}/{configurationName}")
    fun getAssignment(
        @PathVariable("integrationName") integrationName: String,
        @PathVariable("configurationName") configurationName: String
    ): AssignmentResponse
}
