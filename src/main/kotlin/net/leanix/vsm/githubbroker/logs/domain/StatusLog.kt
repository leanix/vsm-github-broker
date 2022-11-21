package net.leanix.vsm.githubbroker.logs.domain

import java.util.UUID

data class StatusLog(
    val runId: UUID,
    val status: LogStatus,
    val message: String?
)
