package net.leanix.vsm.githubbroker.connector.adapter.graphql

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import kotlinx.coroutines.runBlocking
import net.leanix.githubbroker.connector.adapter.graphql.data.AllRepoQuery
import net.leanix.githubbroker.connector.adapter.graphql.data.allrepoquery.LanguageEdge
import net.leanix.githubbroker.connector.adapter.graphql.data.allrepoquery.RepositoryConnection
import net.leanix.githubbroker.connector.adapter.graphql.data.allrepoquery.RepositoryTopic
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.connector.domain.Language
import net.leanix.vsm.githubbroker.connector.domain.PagedRepositories
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.Topic
import net.leanix.vsm.githubbroker.shared.exception.VsmException
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

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

    override fun getAllRepositories(organizationName: String, cursor: String?): Result<PagedRepositories> {
        val query = AllRepoQuery(
            AllRepoQuery.Variables(
                orgName = organizationName,
                pageCount = 1,
                cursor = cursor
            )
        )
        logger.info("started get all repos")
        return runBlocking {
            kotlin.runCatching {
                client.execute(query)
            }.fold(
                {
                    if (it.errors != null && it.errors?.isNotEmpty() == true) {
                        Result.failure(RuntimeException("Error getting data"))
                    } else {
                        parseRepositories(it.data?.organization?.repositories)
                    }
                },
                { Result.failure(it) }
            )
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
                            languages = parseLanguage(repository.languages?.edges) as List<Language>?,
                            topics = parseTopics(repository.repositoryTopics.nodes) as List<Topic>?
                        )
                    }
                )
            )
        } else {
            logger.info("Zero repositories found")
            Result.failure(VsmException.NoRepositoriesFound())
        }
    }

    private fun parseTopics(nodes: List<RepositoryTopic?>?): List<Topic?>? {
        val topics = nodes?.map { repositoryTopic: RepositoryTopic? ->
            repositoryTopic?.topic?.let {
                Topic(
                    it.id,
                    it.name
                )
            }
        }
        return topics
    }

    private fun parseLanguage(edges: List<LanguageEdge?>?): List<Language?>? {
        val languages = edges?.map { languageEdge: LanguageEdge? ->
            languageEdge?.node?.let {
                Language(
                    it.id,
                    it.name,
                    languageEdge.size
                )
            }
        }
        return languages
    }
}
