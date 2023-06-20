package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.githubbroker.connector.domain.RunStatusProvider
import org.springframework.stereotype.Component

@Component
class FeignRunStatusProvider(private val vsmClient: VsmClient) : RunStatusProvider {

    override fun updateRunStatus(runId: String, runState: UpdateRunStateRequest) {
        vsmClient.updateRunState(runId, runState)
    }
}
