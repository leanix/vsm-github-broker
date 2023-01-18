package net.leanix.vsm.githubbroker.connector.domain

import java.util.UUID

data class Assignment(
    val runId: UUID,
    val configurationId: UUID,
    var status: String? = null,
    val organizationName: String
)
