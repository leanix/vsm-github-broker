package net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config

import feign.RequestInterceptor
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_BROKER_VERSION_HEADER
import net.leanix.vsm.githubbroker.shared.auth.application.GetBearerToken
import net.leanix.vsm.githubbroker.shared.properties.GradleProperties.Companion.GITHUB_ENTERPRISE_VERSION
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class MtmFeignClientConfiguration(private val getBearerToken: GetBearerToken) {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header(GITHUB_BROKER_VERSION_HEADER, GITHUB_ENTERPRISE_VERSION)
            it.header(HttpHeaders.AUTHORIZATION, "Bearer ${getBearerToken()}")
        }
    }
}
