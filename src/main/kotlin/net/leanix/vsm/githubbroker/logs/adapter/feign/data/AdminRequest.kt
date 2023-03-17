package net.leanix.vsm.githubbroker.logs.adapter.feign.data

import jakarta.validation.constraints.NotNull
import net.leanix.vsm.githubbroker.logs.domain.AdminLog
import net.leanix.vsm.githubbroker.logs.domain.LogLevel
import java.util.UUID

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
) {
    companion object {
        fun fromDomain(admin: AdminLog): AdminRequest {
            return AdminRequest(
                runId = admin.runId,
                configurationId = admin.configurationId,
                subject = admin.subject,
                level = admin.level,
                message = admin.message,
            )
        }
    }
}
