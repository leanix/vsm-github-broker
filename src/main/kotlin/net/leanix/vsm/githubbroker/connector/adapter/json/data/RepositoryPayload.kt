package net.leanix.vsm.githubbroker.connector.adapter.json.data

data class RepositoryPayload(
    override var action: String,
    val repository: RepositoryData
) : BaseGitHubPayload()