package net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config

import feign.RequestInterceptor
import net.leanix.vsm.githubbroker.shared.auth.application.GetBearerToken
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders

class MtmFeignClientConfiguration(private val getBearerToken: GetBearerToken) {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header(HttpHeaders.AUTHORIZATION, "Bearer ${getBearerToken()}")
        }
    }
}
