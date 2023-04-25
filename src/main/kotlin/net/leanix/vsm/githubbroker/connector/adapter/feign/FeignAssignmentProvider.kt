package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.AssignmentProvider
import org.springframework.stereotype.Component

@Component
class FeignAssignmentProvider(private val vsmClient: VsmClient) : AssignmentProvider {
    override fun getAssignments(
        integrationName: String,
        configSetName: String
    ): Result<List<Assignment>> {
        return kotlin.runCatching {
            vsmClient.getAssignments(integrationName, configSetName).toDomain()
        }
    }
}
