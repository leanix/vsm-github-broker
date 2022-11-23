package net.leanix.vsm.githubbroker.shared.auth.application

import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.AuthClient
import net.leanix.vsm.githubbroker.shared.auth.adapter.feign.properties.AuthenticationProperties
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetBearerToken(
    private val authClient: AuthClient,
    private val authenticationProperties: AuthenticationProperties
) {

    operator fun invoke(): String {
        return authClient.getToken(
            authorization = getBasicAuthHeader(),
            body = "grant_type=client_credentials"
        ).accessToken
    }

    private fun getBasicAuthHeader(): String =
        "Basic " + Base64.getEncoder().encodeToString(
            "${authenticationProperties.clientId}:${authenticationProperties.clientSecret}".toByteArray()
        )
}
