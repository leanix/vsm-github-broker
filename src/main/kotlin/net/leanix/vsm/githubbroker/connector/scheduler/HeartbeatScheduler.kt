package net.leanix.vsm.githubbroker.connector.scheduler

import net.leanix.vsm.githubbroker.connector.adapter.feign.VsmClient
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class HeartbeatScheduler(
    private val vsmClient: VsmClient
) {

    @Scheduled(fixedRate = 300000) // 5 minutes
    fun heartbeat() {
        AssignmentCache.getAll().values.forEach { assigment -> vsmClient.heartbeat(assigment.runId.toString()) }
    }
}
