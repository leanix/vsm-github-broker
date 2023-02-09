package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.GitHubWebhookRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.GitHubWebhookResponse
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config.GitHubFeignClientConfiguration
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "GitHubClient",
    url = "\${leanix.vsm.connector.github-url}/api/\${leanix.vsm.connector.github-version}",
    configuration = [GitHubFeignClientConfiguration::class]
)
interface GitHubClient {

    @GetMapping("/orgs/{org}/hooks")
    fun getHooks(@PathVariable org: String): List<GitHubWebhookResponse>

    @DeleteMapping("/orgs/{org}/hooks/{id}")
    fun deleteHook(@PathVariable org: String, @PathVariable id: String)

    @PostMapping("/orgs/{org}/hooks")
    fun createHook(@PathVariable org: String, @RequestBody body: GitHubWebhookRequest): GitHubWebhookResponse
}
