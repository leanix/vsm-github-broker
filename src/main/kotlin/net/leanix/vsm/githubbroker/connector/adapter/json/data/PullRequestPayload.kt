package net.leanix.vsm.githubbroker.connector.adapter.json.data

import com.fasterxml.jackson.annotation.JsonProperty

data class PullRequestPayload(
    override var action: String,
    @JsonProperty("pull_request")
    val pullRequest: PullRequestData,
    val repository: RepositoryData
) : BaseGitHubPayload()
