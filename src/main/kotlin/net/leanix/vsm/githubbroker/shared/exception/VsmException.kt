package net.leanix.vsm.githubbroker.shared.exception

sealed class VsmException(message: String? = null) : RuntimeException(message) {

    class NoRepositoriesFound : VsmException()

    class WebhookRegistrationFailed(message: String) : VsmException(message)
}
