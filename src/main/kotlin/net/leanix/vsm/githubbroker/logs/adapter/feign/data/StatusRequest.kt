package net.leanix.vsm.githubbroker.logs.adapter.feign.data

import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import java.util.UUID
import javax.validation.constraints.NotNull

data class StatusRequest(
    @field:NotNull(message = "Field \"runId\" cannot be empty")
    val runId: UUID?,
    @field:NotNull(message = "Field \"status\" cannot be empty")
    val status: LogStatus?,
    val message: String? = null
)
