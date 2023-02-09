package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import net.leanix.vsm.githubbroker.connector.domain.Language
import net.leanix.vsm.githubbroker.connector.domain.Topic
import java.util.UUID

data class ServiceRequest(
    val id: String,
    val runId: UUID,
    val source: String,
    val name: String,
    val description: String?,
    val url: String?,
    val archived: Boolean?,
    val visibility: String?,
    val organizationName: String,
    val languages: List<Language>? = null,
    val labels: List<Topic>? = null,
    val contributors: List<String>? = null
)
