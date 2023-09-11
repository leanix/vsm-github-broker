package net.leanix.vsm.githubbroker.shared.auth.adapter.feign.config

import feign.RequestInterceptor
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_BROKER_VERSION_HEADER
import net.leanix.vsm.githubbroker.shared.Constants.HOSTNAME_HEADER
import net.leanix.vsm.githubbroker.shared.auth.application.GetBearerToken
import net.leanix.vsm.githubbroker.shared.properties.GradleProperties.Companion.GITHUB_ENTERPRISE_VERSION
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import java.net.InetAddress

class MtmFeignClientConfiguration(private val getBearerToken: GetBearerToken) {

    private val logger: Logger = LoggerFactory.getLogger(MtmFeignClientConfiguration::class.java)

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header(GITHUB_BROKER_VERSION_HEADER, GITHUB_ENTERPRISE_VERSION)
            it.header(HttpHeaders.AUTHORIZATION, "Bearer ${getBearerToken()}")
            it.header(HOSTNAME_HEADER, getHostName())
        }
    }

    private fun getHostName(): String {
        return runCatching {
            InetAddress.getLocalHost().hostName
        }.onFailure {
            logger.error("Failed get hostname", it)
        }.getOrElse { "empty-hostname" }
    }
}
