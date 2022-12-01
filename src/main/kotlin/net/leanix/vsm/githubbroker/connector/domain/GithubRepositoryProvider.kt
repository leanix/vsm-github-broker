package net.leanix.vsm.githubbroker.connector.domain

interface GithubRepositoryProvider {
    fun getAllRepositories(organizationName: String, cursor: String?): Result<PagedRepositories>
}
