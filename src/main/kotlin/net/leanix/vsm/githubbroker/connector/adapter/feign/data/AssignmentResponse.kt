package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import java.util.*

data class AssignmentResponse(
    val runId: UUID,
    val configurationId: UUID,
    val workspaceId: UUID,
    var status: String? = null,
    val organizationNameList: List<String>
) {

    fun toDomain(): List<Assignment> {
        return organizationNameList.map { organizationName ->
            Assignment(
                runId = runId,
                workspaceId = workspaceId,
                configurationId = configurationId,
                organizationName = organizationName
            )
        }
    }
}
