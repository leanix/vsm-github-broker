package net.leanix.vsm.githubbroker.logs.adapter.feign

import net.leanix.vsm.githubbroker.logs.adapter.feign.data.StatusRequest
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.MtmFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "statusLoggingClient",
    url = "\${leanix.vsm.base-url}",
    configuration = [MtmFeignClientConfiguration::class]
)
interface StatusLogClient {

    @PostMapping(value = ["/log/connector-status"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun sendStatusLog(@RequestBody request: StatusRequest)
}
