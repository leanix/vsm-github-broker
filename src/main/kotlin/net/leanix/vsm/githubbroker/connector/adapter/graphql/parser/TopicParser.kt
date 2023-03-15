package net.leanix.vsm.githubbroker.connector.adapter.graphql.parser

import net.leanix.githubbroker.connector.adapter.graphql.data.allrepoquery.RepositoryTopic
import net.leanix.vsm.githubbroker.connector.domain.Topic
import net.leanix.githubbroker.connector.adapter.graphql.data.getlangtopicsquery.RepositoryTopic as LangRepositoryTopic

object TopicParser {

    fun parseRepoTopic(nodes: List<RepositoryTopic?>?): List<Topic>? {
        return if (!nodes.isNullOrEmpty()) {
            nodes.filterNotNull().map { repositoryTopic ->
                repositoryTopic.let {
                    Topic(
                        it.topic.id,
                        it.topic.name
                    )
                }
            }
        } else {
            null
        }
    }

    fun parse(nodes: List<LangRepositoryTopic?>?): List<Topic>? {
        return if (!nodes.isNullOrEmpty()) {
            nodes.filterNotNull().map { repositoryTopic ->
                repositoryTopic.let {
                    Topic(
                        it.topic.id,
                        it.topic.name
                    )
                }
            }
        } else {
            null
        }
    }
}
