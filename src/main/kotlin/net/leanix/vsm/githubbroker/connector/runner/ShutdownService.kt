package net.leanix.vsm.githubbroker.connector.runner

import jakarta.annotation.PreDestroy
import net.leanix.vsm.githubbroker.connector.adapter.feign.FeignRunStatusProvider
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.RunState
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE_CONNECTOR
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShutdownService(private val runStatusProvider: FeignRunStatusProvider) {

    private val logger = LoggerFactory.getLogger(ShutdownService::class.java)

    @PreDestroy
    fun onDestroy() {
        if (AssignmentCache.getAll().isEmpty()) {
            logger.info("Shutting down github broker before receiving any assignment")
        } else {
            AssignmentCache.getAll().values.forEach { assignment ->
                runStatusProvider.updateRunStatus(
                    assignment.runId.toString(),
                    UpdateRunStateRequest(
                        state = RunState.FINISHED,
                        workspaceId = assignment.workspaceId.toString(),
                        connector = GITHUB_ENTERPRISE_CONNECTOR,
                        orgName = assignment.organizationName,
                        message = "Gracefully stopped github enterprise"
                    )
                )
            }
        }
    }
}
