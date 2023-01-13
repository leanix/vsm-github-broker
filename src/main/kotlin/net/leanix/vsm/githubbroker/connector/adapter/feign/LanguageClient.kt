package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.LanguageRequest
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.FeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping

@FeignClient(
    name = "servicesClient",
    url = "\${leanix.vsm.event-broker.base-url}",
    configuration = [FeignClientConfiguration::class]
)
interface LanguageClient {

    @PostMapping("/languages")
    fun saveLanguage(languageRequest: LanguageRequest)
}
