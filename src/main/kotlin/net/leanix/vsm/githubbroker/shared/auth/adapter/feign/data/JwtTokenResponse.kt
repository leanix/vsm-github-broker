package net.leanix.vsm.githubbroker.shared.auth.adapter.feign.data

import com.fasterxml.jackson.annotation.JsonProperty

data class JwtTokenResponse(
    val scope: String,
    val expired: Boolean,
    @JsonProperty("access_token")
    val accessToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expired_in")
    val expiredIn: Int
)
