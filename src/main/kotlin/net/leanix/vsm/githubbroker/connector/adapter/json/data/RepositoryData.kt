package net.leanix.vsm.githubbroker.connector.adapter.json.data

import com.fasterxml.jackson.annotation.JsonProperty

data class RepositoryData(
    @JsonProperty("node_id")
    val id: String,
    val name: String,
    val archived: Boolean,
    val description: String?,
    val url: String,
    val visibility: String,
    val topics: List<String>?,
    val language: String?,
    @JsonProperty("default_branch")
    val defaultBranch: String
)
