package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import java.util.*

data class AssignmentResponse(
    val runId: UUID,
    val configurationId: UUID
) {

    fun toDomain(): Assignment {
        return Assignment(
            runId = runId,
            configurationId = configurationId
        )
    }
}
