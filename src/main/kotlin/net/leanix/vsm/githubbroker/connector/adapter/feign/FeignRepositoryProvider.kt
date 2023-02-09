package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.ServiceRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.RepositoryProvider
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE
import org.springframework.stereotype.Component

@Component
class FeignRepositoryProvider(private val vsmClient: VsmClient) : RepositoryProvider {

    override fun save(repository: Repository, assignment: Assignment) {
        val service = ServiceRequest(
            id = repository.id,
            runId = assignment.runId,
            source = GITHUB_ENTERPRISE,
            name = repository.name,
            description = repository.description,
            url = repository.url,
            archived = repository.archived,
            visibility = repository.visibility,
            languages = repository.languages,
            labels = repository.topics,
            contributors = emptyList(),
            organizationName = assignment.organizationName
        )

        vsmClient.saveService(service)
    }
}
