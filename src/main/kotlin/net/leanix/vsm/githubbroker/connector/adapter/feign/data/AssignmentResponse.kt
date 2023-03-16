package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import java.util.*

data class AssignmentResponse(
    val runId: UUID,
    val workspaceId: UUID,
    val configurationId: UUID,
    val connectorConfiguration: List<ConfigField>
) {

    fun toDomain(): Assignment {
        val organizationName = connectorConfiguration.first { it.id == "orgName" }

        return Assignment(
            runId = runId,
            workspaceId = workspaceId,
            configurationId = configurationId,
            organizationName = organizationName.value as String
        )
    }
}
