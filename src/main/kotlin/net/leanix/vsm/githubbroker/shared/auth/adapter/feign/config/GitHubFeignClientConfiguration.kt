package net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config

import feign.RequestInterceptor
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class GitHubFeignClientConfiguration(private val vsmProperties: VsmProperties) {

    private val gitHubApiVersion = "2022-11-28"

    @Bean
    fun gitHubRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header(HttpHeaders.AUTHORIZATION, "Bearer ${vsmProperties.githubToken}")
            it.header("X-GitHub-Api-Version", gitHubApiVersion)
        }
    }
}
