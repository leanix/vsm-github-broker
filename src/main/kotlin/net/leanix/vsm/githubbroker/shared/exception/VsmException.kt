package net.leanix.vsm.githubbroker.shared.exception

sealed class VsmException(message: String? = null) : RuntimeException(message) {

    class NoRepositoriesFound : VsmException()

    class WebhookRegistrationFailed(message: String) : VsmException(message)

    class GraphqlException(message: String?) : VsmException(message)

    class ParsePayloadFailed(message: String?) : VsmException(message)

    class WebhookEventOrActionNotSupported(message: String) : VsmException(message)

    class WebhookEventValidationFailed(message: String) : VsmException(message)
}
