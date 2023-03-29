package net.leanix.vsm.githubbroker.connector.adapter.graphql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.coroutines.runBlocking
import net.leanix.githubbroker.connector.adapter.graphql.data.AllRepoQuery
import net.leanix.githubbroker.connector.adapter.graphql.data.GetLangTopicsQuery
import net.leanix.githubbroker.connector.adapter.graphql.data.allrepoquery.RepositoryConnection
import net.leanix.vsm.githubbroker.connector.adapter.graphql.parser.GithubPullRequestParser
import net.leanix.vsm.githubbroker.connector.adapter.graphql.parser.LanguageParser
import net.leanix.vsm.githubbroker.connector.adapter.graphql.parser.TopicParser
import net.leanix.vsm.githubbroker.connector.domain.Dora
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.connector.domain.Language
import net.leanix.vsm.githubbroker.connector.domain.PagedRepositories
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.Topic
import net.leanix.vsm.githubbroker.shared.exception.VsmException
import net.leanix.vsm.githubbroker.shared.extensions.flatMap
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import net.leanix.githubbroker.connector.adapter.graphql.`data`.getlangtopicsquery.Repository as LangTopicRepository
@Component
class GraphqlGithubRepositoryProvider(vsmProperties: VsmProperties) : GithubRepositoryProvider {

    private val logger: Logger = LoggerFactory.getLogger(GraphqlGithubRepositoryProvider::class.java)

    private val client = GraphQLWebClient(
        url = vsmProperties.githubUrl + "/api/graphql",
        builder = WebClient.builder()
            .defaultHeaders {
                it.set(HttpHeaders.AUTHORIZATION, "Bearer ${vsmProperties.githubToken}")
            }
    )

    private val githubPullRequestParser = GithubPullRequestParser(client)

    override fun getAllRepositories(organizationName: String, cursor: String?): Result<PagedRepositories> {
        val query = AllRepoQuery(
            AllRepoQuery.Variables(
                orgName = organizationName,
                pageCount = 100,
                cursor = cursor
            )
        )
        logger.info("Getting next page of repositories")
        return kotlin.runCatching {
            executeQuery(query)
        }.flatMap {
            if (it.errors != null && it.errors?.isNotEmpty() == true) {
                if (it.data?.organization == null) {
                    Result.failure(VsmException.NoRepositoriesFound())
                } else {
                    Result.failure(RuntimeException("Error getting data"))
                }
            } else {
                parseRepositories(it.data?.organization?.repositories)
            }
        }
    }

    private fun parseRepositories(repositories: RepositoryConnection?): Result<PagedRepositories> {
        return if (repositories?.nodes != null && repositories.nodes.isNotEmpty()) {
            Result.success(
                PagedRepositories(
                    hasNextPage = repositories.pageInfo.hasNextPage,
                    cursor = repositories.pageInfo.endCursor,
                    repositories = repositories.nodes.filterNotNull().map { repository ->
                        Repository(
                            id = repository.id,
                            name = repository.name,
                            description = repository.description,
                            archived = repository.isArchived,
                            url = repository.url,
                            visibility = repository.visibility.name.lowercase(),
                            languages = LanguageParser.parseRepoLanguages(repository.languages?.edges),
                            topics = TopicParser.parseRepoTopic(repository.repositoryTopics.nodes),
                            defaultBranch = repository.defaultBranchRef?.name ?: "empty-branch"
                        )
                    }
                )
            )
        } else {
            logger.info("Zero repositories found")
            Result.failure(VsmException.NoRepositoriesFound())
        }
    }

    override fun getDoraRawData(repository: Repository, periodInDays: String): Result<List<Dora>> {
        return kotlin.runCatching {
            val requests = githubPullRequestParser.getPagedPullRequests(repository, periodInDays)
            requests
                .filter { it.mergedAt >= periodInDays }
                .map {
                    Dora(
                        repositoryName = repository.name,
                        repositoryUrl = repository.url,
                        pullRequest = it.copy(
                            commits = githubPullRequestParser.getPullRequestsCommits(it)
                        )
                    )
                }
        }
    }

    override fun getLanguagesAndTopics(repositoryId: String): Result<Pair<List<Topic>?, List<Language>?>> {
        return kotlin.runCatching {
            val query = GetLangTopicsQuery(
                GetLangTopicsQuery.Variables(
                    repoId = repositoryId
                )
            )
            val response = executeQuery(query)
            when {
                !response.errors.isNullOrEmpty() -> {
                    throw VsmException.GraphqlException(response.errors!!.map { it.message }.joinToString { it })
                }
                else -> {
                    val repository = response.data!!.node as LangTopicRepository
                    val languages = LanguageParser.parse(repository.languages?.edges)
                    val topics = TopicParser.parse(repository.repositoryTopics.nodes)
                    Pair(topics, languages)
                }
            }
        }
    }

    private fun <T : Any> executeQuery(query: GraphQLClientRequest<T>): GraphQLClientResponse<T> {
        return runBlocking {
            client.execute(query)
        }
    }
}
