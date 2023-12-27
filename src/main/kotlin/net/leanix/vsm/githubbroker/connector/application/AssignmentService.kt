package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.AssignmentProvider
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE_CONNECTOR
import net.leanix.vsm.githubbroker.shared.properties.VsmProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class AssignmentService(
    private val assignmentProvider: AssignmentProvider,
    private val vsmProperties: VsmProperties
) {

    private val logger = LoggerFactory.getLogger(AssignmentService::class.java)

    fun getAssignments(): List<Assignment> {
        return assignmentProvider.getAssignments(
            GITHUB_ENTERPRISE_CONNECTOR,
            vsmProperties.configSetName
        ).onFailure {
            logger.error("Failed to retrieve assignment list: ", it)
        }.onSuccess {
            logger.info("Assignment list retrieved with success with ${it.size} assignments")
        }.getOrThrow()
    }
}
