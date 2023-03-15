package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraChangeEventData
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraChangeEventRequest
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraReleaseEventData
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.DoraReleaseEventRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Dora
import net.leanix.vsm.githubbroker.connector.domain.DoraProvider
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class FeignDoraProvider(private val doraClient: DoraClient) : DoraProvider {

    private val logger = LoggerFactory.getLogger(FeignDoraProvider::class.java)

    override fun saveDora(dora: Dora, assignment: Assignment) {
        kotlin.runCatching {
            dora.pullRequest
                .commits
                .forEach {
                    val changeEventRequest = DoraChangeEventRequest(
                        id = it.id,
                        sourceType = GITHUB_ENTERPRISE,
                        sourceInstance = assignment.organizationName,
                        serviceName = dora.repositoryName,
                        data = DoraChangeEventData(
                            name = it.author.name,
                            email = it.author.email,
                            username = it.author.username,
                            changeTime = it.changeTime
                        )
                    )
                    doraClient.saveChangeEvent(changeEventRequest)
                }
            val releaseEventRequest = DoraReleaseEventRequest(
                id = dora.pullRequest.id,
                sourceType = GITHUB_ENTERPRISE,
                sourceInstance = assignment.organizationName,
                serviceName = dora.repositoryName,
                data = DoraReleaseEventData(
                    changeIds = dora.pullRequest.changeIds(),
                    releaseTime = dora.pullRequest.mergedAt
                )
            )
            doraClient.saveReleaseEvent(releaseEventRequest)
        }.onFailure { logger.error("Failed save dora events: ${dora.repositoryName}", it) }
    }
}
