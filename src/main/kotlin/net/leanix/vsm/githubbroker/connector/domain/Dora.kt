package net.leanix.vsm.githubbroker.connector.domain

data class Dora(
    val repositoryName: String,
    val pullRequest: PullRequest
)

data class PullRequest(
    val id: String,
    val baseRefName: String,
    val mergeAt: String,
    val commits: List<Commit> = emptyList()
) {
    fun changeIds() = commits.map { it.id }
}

data class Commit(
    val id: String,
    val changeTime: String,
    val author: Author
)

data class Author(
    val name: String,
    val email: String,
    val username: String?
)
