package net.leanix.vsm.githubbroker.shared.auth.adapter.feign

import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.data.JwtTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "authentication",
    url = "\${leanix.vsm.auth.access-token-uri}"
)
interface AuthClient {

    @PostMapping(value = ["/oauth2/token"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun getToken(
        @RequestHeader(name = HttpHeaders.AUTHORIZATION) authorization: String,
        @RequestBody body: String
    ): JwtTokenResponse
}
