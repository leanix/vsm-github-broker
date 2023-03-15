package net.leanix.vsm.githubbroker.connector.domain

interface GithubRepositoryProvider {
    fun getAllRepositories(organizationName: String, cursor: String?): Result<PagedRepositories>

    fun getDoraRawData(repository: Repository, periodInDays: String): Result<List<Dora>>

    fun getLanguagesAndTopics(repositoryId: String): Result<Pair<List<Topic>?, List<Language>?>>
}
