package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.AssignmentProvider
import net.leanix.vsm.githubbroker.shared.Constants
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AssignmentService(
    private val assignmentProvider: AssignmentProvider,
    private val vsmProperties: VsmProperties
) {

    private val logger = LoggerFactory.getLogger(AssignmentService::class.java)

    fun getAssignment(): Assignment {
        return assignmentProvider.getAssignment(
            Constants.GITHUB_ENTERPRISE_CONNECTOR,
            vsmProperties.configName
        ).onFailure {
            logger.error("Failed to retrieve assignment: ", it)
        }.onSuccess {
            logger.info("Assignment retrieved with success: ${it.runId}")
        }.getOrThrow()
    }
}
