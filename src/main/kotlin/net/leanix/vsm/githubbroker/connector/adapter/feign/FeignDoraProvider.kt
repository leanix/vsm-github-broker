package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Dora
import net.leanix.vsm.githubbroker.connector.domain.DoraProvider
import net.leanix.vsm.githubbroker.connector.domain.Repository
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE_CONNECTOR
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FeignDoraProvider(private val vsmClient: VsmClient) : DoraProvider {

    private val logger = LoggerFactory.getLogger(FeignDoraProvider::class.java)

    override fun saveDora(dora: Dora, assignment: Assignment, repository: Repository) {
        kotlin.runCatching {
            vsmClient.saveDora(
                DoraRequest(
                    repositoryId = repository.id,
                    repositoryName = repository.name,
                    repositoryUrl = repository.url,
                    connectorType = GITHUB_ENTERPRISE_CONNECTOR,
                    orgName = assignment.organizationName,
                    runId = assignment.runId,
                    configurationId = assignment.configurationId,
                    pullRequest = dora.pullRequest
                )
            )
        }.onFailure { logger.error("Failed save dora events: ${dora.repositoryName}", it) }
    }
}
