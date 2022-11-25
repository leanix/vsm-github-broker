package net.leanix.vsm.githubbroker.logs.adapter.feign

import net.leanix.vsm.githubbroker.logs.adapter.feign.data.AdminRequest
import net.leanix.vsm.githubbroker.logs.adapter.feign.data.StatusRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "loggingClient",
    url = "\${leanix.vsm.event-broker.base-url}"
)
interface LoggingClient {
    @PostMapping(value = ["/logs/status"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun sendStatusLog(@RequestBody request: StatusRequest)

    @PostMapping(value = ["/logs/admin"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun sendAdminLog(@RequestBody request: AdminRequest)
}
