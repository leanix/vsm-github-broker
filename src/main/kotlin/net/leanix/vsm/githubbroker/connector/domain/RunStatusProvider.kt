package net.leanix.vsm.githubbroker.connector.domain

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.UpdateRunStateRequest

interface RunStatusProvider {
    fun updateRunStatus(runId: String, runState: UpdateRunStateRequest)
}
