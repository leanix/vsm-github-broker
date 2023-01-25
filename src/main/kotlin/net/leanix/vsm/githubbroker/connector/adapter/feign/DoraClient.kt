package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraChangeEventRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraReleaseEventRequest
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.FeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(
    name = "doraClient",
    url = "\${leanix.vsm.dora.base-url}",
    configuration = [FeignClientConfiguration::class]
)
interface DoraClient {

    @PostMapping("/change")
    fun saveChangeEvent(doraChangeEventRequest: DoraChangeEventRequest)

    @PostMapping("/release")
    fun saveReleaseEvent(doraReleaseEventRequest: DoraReleaseEventRequest)
}
