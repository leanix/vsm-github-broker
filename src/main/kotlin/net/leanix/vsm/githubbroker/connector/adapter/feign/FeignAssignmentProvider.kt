package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.AssignmentProvider
import org.springframework.stereotype.Component

@Component
class FeignAssignmentProvider(private val assignmentClient: AssignmentClient) : AssignmentProvider {
    override fun getAssignments(integrationName: String): Result<List<Assignment>> {
        return kotlin.runCatching {
            assignmentClient.getAssignments(integrationName)
        }.map { assignmentList ->
            assignmentList.map { it.toDomain() }
        }
    }
}
