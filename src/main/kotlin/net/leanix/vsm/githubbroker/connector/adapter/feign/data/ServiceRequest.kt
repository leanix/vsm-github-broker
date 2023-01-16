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
    val languages: List<Language>?,
    val labels: List<Topic>?
)
