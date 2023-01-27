package net.leanix.vsm.githubbroker.connector.adapter.graphql.parser

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.client.types.GraphQLClientRequest
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import kotlinx.coroutines.runBlocking
import net.leanix.githubbroker.connector.adapter.graphql.data.GetPullRequestCommits
import net.leanix.githubbroker.connector.adapter.graphql.data.GetReposPullRequestsQuery
import net.leanix.githubbroker.connector.adapter.graphql.data.getpullrequestcommits.PullRequestCommitConnection
import net.leanix.githubbroker.connector.adapter.graphql.data.getrepospullrequestsquery.PullRequestConnection
import net.leanix.vsm.githubbroker.connector.domain.Author
import net.leanix.vsm.githubbroker.connector.domain.Commit
import net.leanix.vsm.githubbroker.connector.domain.PullRequest
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.shared.exception.VsmException.GraphqlException
import net.leanix.githubbroker.connector.adapter.graphql.data.getpullrequestcommits.PullRequest as PullRequestCommit
import net.leanix.githubbroker.connector.adapter.graphql.data.getrepospullrequestsquery.Repository as PullRequestRepository

class GithubPullRequestParser(private val client: GraphQLWebClient) {

    fun getPagedPullRequests(repository: Repository, totalPassedDays: String): List<PullRequest> {
        var cursor: String? = null
        var hasNextPage: Boolean
        var mergedPullRequests = emptyList<PullRequest>()

        do {
            val query = GetReposPullRequestsQuery(
                GetReposPullRequestsQuery.Variables(
                    repoId = repository.id,
                    pullReqPageCount = 100,
                    defaultBranch = repository.defaultBranch,
                    cursor = cursor
                )
            )
            val response = executeQuery(query)
            when {
                !response.errors.isNullOrEmpty() -> {
                    throw GraphqlException(response.errors!!.map { it.message }.joinToString { it })
                }
                else -> {
                    val pullRequestRepository = response.data!!.node as PullRequestRepository
                    val pullRequests = pullRequestRepository.pullRequests.nodes
                    mergedPullRequests = mergedPullRequests + parsePullRequests(pullRequestRepository.pullRequests)
                    if (pullRequests!!.last()!!.mergedAt!! >= totalPassedDays) {
                        return mergedPullRequests
                    }
                    cursor = pullRequestRepository.pullRequests.pageInfo.endCursor
                    hasNextPage = pullRequestRepository.pullRequests.pageInfo.hasNextPage
                }
            }
        } while (hasNextPage)

        return mergedPullRequests
    }

    private fun parsePullRequests(pullRequests: PullRequestConnection): List<PullRequest> {
        return if (!pullRequests.nodes.isNullOrEmpty()) {
            pullRequests.nodes.filterNotNull().map { pullRequest ->
                PullRequest(
                    id = pullRequest.id,
                    baseRefName = pullRequest.baseRefName,
                    mergedAt = pullRequest.mergedAt!!
                )
            }
        } else {
            emptyList()
        }
    }

    fun getPullRequestsCommits(pullRequest: PullRequest): List<Commit> {
        return getPagedPullRequestsCommits(pullRequest.id)
    }

    private fun getPagedPullRequestsCommits(pullRequestId: String): List<Commit> {
        var cursor: String? = null
        var hasNextPage: Boolean
        var mergedCommits = emptyList<Commit>()

        do {
            val query = GetPullRequestCommits(
                GetPullRequestCommits.Variables(
                    pullReqId = pullRequestId,
                    initialCommitPageCount = 100,
                    cursor = cursor
                )
            )
            val response = executeQuery(query)
            when {
                !response.errors.isNullOrEmpty() -> {
                    throw GraphqlException(response.errors!!.map { it.message }.joinToString { it })
                }
                else -> {
                    val pullRequestCommit = response.data!!.node as PullRequestCommit
                    val commits = pullRequestCommit.commits
                    mergedCommits = mergedCommits + parseCommits(commits)

                    cursor = commits.pageInfo.endCursor
                    hasNextPage = commits.pageInfo.hasNextPage
                }
            }
        } while (hasNextPage)

        return mergedCommits
    }

    private fun parseCommits(pullRequestCommit: PullRequestCommitConnection): List<Commit> {
        return if (!pullRequestCommit.nodes.isNullOrEmpty()) {
            pullRequestCommit.nodes.filterNotNull().map { commit ->
                Commit(
                    id = commit.commit.id,
                    changeTime = commit.commit.committedDate,
                    author = Author(
                        name = commit.commit.author!!.name!!,
                        email = commit.commit.author.email!!,
                        username = commit.commit.author.user!!.login
                    )
                )
            }
        } else {
            emptyList()
        }
    }

    private fun <T : Any> executeQuery(query: GraphQLClientRequest<T>): GraphQLClientResponse<T> {
        return runBlocking {
            client.execute(query)
        }
    }
}
