package net.leanix.vsm.githubbroker.logs.adapter.data

import net.leanix.vsm.githubbroker.logs.domain.LogLevel
import java.util.UUID
import javax.validation.constraints.NotNull

data class AdminRequest(
    @field:NotNull(message = "Field \"runId\" cannot be empty")
    val runId: UUID?,
    @field:NotNull(message = "Field \"configurationId\" cannot be empty")
    val configurationId: UUID?,
    @field:NotNull(message = "Field \"subject\" cannot be empty")
    val subject: String?,
    @field:NotNull(message = "Field \"level\" cannot be empty")
    val level: LogLevel?,
    @field:NotNull(message = "Field \"message\" cannot be empty")
    val message: String?
)
