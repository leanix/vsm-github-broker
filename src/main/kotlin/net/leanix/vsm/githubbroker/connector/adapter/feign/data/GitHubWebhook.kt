package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import com.fasterxml.jackson.annotation.JsonProperty

data class GitHubWebhookResponse(
    val id: String,
    val name: String,
    val active: Boolean,
    val events: List<String>,
    val config: Config
)

data class Config(
    val url: String,
    @JsonProperty("content_type")
    val contentType: String,
)

data class GitHubWebhookRequest(
    val name: String = "web",
    val active: Boolean = true,
    val events: List<String>,
    val config: Config
)
