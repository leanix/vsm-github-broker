package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.CommandRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.CommandEventAction
import net.leanix.vsm.githubbroker.connector.domain.CommandProvider
import org.springframework.stereotype.Component

@Component
class FeignCommandProvider(private val vsmClient: VsmClient) : CommandProvider {

    override fun sendCommand(assignment: Assignment, action: CommandEventAction) {
        val command = CommandRequest(
            type = "command",
            action = action.action,
            scope = buildScope(assignment)
        )

        vsmClient.sendCommand(command)
    }

    private fun buildScope(assignment: Assignment): String {
        return "workspace/${assignment.workspaceId}/configuration/${assignment.configurationId}"
    }
}
