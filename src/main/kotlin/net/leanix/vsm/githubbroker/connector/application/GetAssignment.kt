package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.adapter.feign.FeignAssignmentProvider
import net.leanix.vsm.githubbroker.connector.domain.AssignmentProvider
import net.leanix.vsm.githubbroker.connector.properties.VsmProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetAssignment(
    private val assignmentProvider: AssignmentProvider,
    private val vsmProperties: VsmProperties
) {

    private val logger = LoggerFactory.getLogger(FeignAssignmentProvider::class.java)

    operator fun invoke() {
        assignmentProvider.getAssignment(
            "github-on-prem-repository-connector",
            vsmProperties.configName
        ).onFailure {
            logger.error("failed to retrieve assignment: ", it)
        }.onSuccess {
            logger.info("Assignment retrieved with success: ${it.runId}")
        }
    }
}
