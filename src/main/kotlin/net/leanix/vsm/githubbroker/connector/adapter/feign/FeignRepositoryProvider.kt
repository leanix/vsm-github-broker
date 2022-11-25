package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.ServiceRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.connector.domain.RepositoryProvider
import org.springframework.stereotype.Component

@Component
class FeignRepositoryProvider(private val serviceClient: ServiceClient) : RepositoryProvider {

    override fun save(repository: Repository, assignment: Assignment) {
        val service = ServiceRequest(
            id = repository.id,
            runId = assignment.runId,
            source = "source",
            name = repository.name,
            description = repository.description,
            url = repository.url,
            archived = repository.archived
        )

        serviceClient.saveService(service)
    }
}