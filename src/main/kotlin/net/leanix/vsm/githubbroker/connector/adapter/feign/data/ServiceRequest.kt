package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import java.util.UUID

data class ServiceRequest(
    val id: String,
    val runId: UUID,
    val source: String,
    val name: String,
    val description: String?,
    val url: String?,
    val archived: Boolean?,
    val visibility: String?
)
