package net.leanix.vsm.githubbroker.logs.domain

import java.util.UUID

data class AdminLog(
    val runId: UUID,
    val configurationId: UUID,
    val subject: String,
    val level: LogLevel,
    val message: String?
)
