package net.leanix.vsm.githubbroker.adapter.output.client

import net.leanix.vsm.githubbroker.adapter.dto.AdminRequest
import net.leanix.vsm.githubbroker.adapter.dto.StatusRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "loggingClient",
    url = "\${leanix.vsm.event-broker.base-url}"
)
interface LoggingClient {
    @PostMapping(value = ["/status"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun statusLog(@RequestBody request: StatusRequest)

    @PostMapping(value = ["/admin"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun adminLog(@RequestBody request: AdminRequest)
}
