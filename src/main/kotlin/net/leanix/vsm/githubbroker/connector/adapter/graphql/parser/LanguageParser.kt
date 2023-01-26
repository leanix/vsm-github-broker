package net.leanix.vsm.githubbroker.connector.adapter.graphql.parser

import net.leanix.vsm.githubbroker.connector.domain.Language
import net.leanix.githubbroker.connector.adapter.graphql.`data`.allrepoquery.LanguageEdge as RepoLanguageEdge
import net.leanix.githubbroker.connector.adapter.graphql.`data`.getlangtopicsquery.LanguageEdge as LangTopicLanguageEdge

object LanguageParser {

    fun parse(edges: List<LangTopicLanguageEdge?>?): List<Language>? {
        return if (!edges.isNullOrEmpty()) {
            edges.filterNotNull().map { languageEdge ->
                languageEdge.node.let {
                    Language(
                        it.id,
                        it.name,
                        languageEdge.size
                    )
                }
            }
        } else {
            null
        }
    }

    fun parseRepoLanguages(edges: List<RepoLanguageEdge?>?): List<Language>? {
        return if (!edges.isNullOrEmpty()) {
            edges.filterNotNull().map { languageEdge ->
                languageEdge.node.let {
                    Language(
                        it.id,
                        it.name,
                        languageEdge.size
                    )
                }
            }
        } else {
            null
        }
    }
}
