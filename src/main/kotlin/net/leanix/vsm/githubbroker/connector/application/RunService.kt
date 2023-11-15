package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.adapter.feign.FeignRunStatusProvider
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.RunState
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.shared.Constants
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class RunService(
    private val assignmentService: AssignmentService,
    private val runStatusProvider: FeignRunStatusProvider,
) {

    private val logger = LoggerFactory.getLogger(RunService::class.java)

    fun getAssignments(): List<Assignment>? {
        kotlin.runCatching {
            finishRuns("Finish runs").let {
                AssignmentCache.deleteAll()
                val assignments = assignmentService.getAssignments()
                println(assignments.first().workspaceId)
                AssignmentCache.addAll(assignments)
                return assignments
            }
        }.onFailure {
            logger.error(it.message)
            logger.error("Failed to get initial state. No assignment found for this workspace id")
        }
        return null
    }

    fun finishRuns(message: String) {
        runCatching {
            if (AssignmentCache.getAll().isEmpty()) {
                logger.info("$message before receiving any assignment")
            } else {
                AssignmentCache.getAll().values.forEach { assignment ->
                    runStatusProvider.updateRunStatus(
                        assignment.runId.toString(),
                        UpdateRunStateRequest(
                            state = RunState.FINISHED,
                            workspaceId = assignment.workspaceId.toString(),
                            connector = Constants.GITHUB_ENTERPRISE_CONNECTOR,
                            orgName = assignment.organizationName,
                            message = message,
                        ),
                    )
                }
            }
        }.onFailure {
            logger.error("Error finishing run: ", it)
        }
    }
}
