package net.leanix.vsm.githubbroker.connector.domain

data class PagedRepositories(
    val repositories: List<Repository>,
    val hasNextPage: Boolean,
    val cursor: String? = null
)
