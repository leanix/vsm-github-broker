package net.leanix.vsm.githubbroker.connector.runner

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.leanix.vsm.githubbroker.connector.adapter.feign.FeignRunStatusProvider
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.RunState.FINISHED
import net.leanix.vsm.githubbroker.connector.adapter.feign.data.UpdateRunStateRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE_CONNECTOR
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ShutdownServiceTest {
    private val runStatusProvider: FeignRunStatusProvider = mockk(relaxed = true)
    private val shutdownService = ShutdownService(runStatusProvider)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        AssignmentCache.deleteAll()
    }

    @Test
    fun `test onDestroy with empty assignment cache`() {
        shutdownService.onDestroy()

        // Assert no interactions with the run status provider
        verify(exactly = 0) { runStatusProvider.updateRunStatus(any(), any()) }
    }

    @Test
    fun `test onDestroy with assignments`() {
        val assignment = Assignment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, "mock-org")
        AssignmentCache.addAll(listOf(assignment))

        val runStateSlot = slot<UpdateRunStateRequest>()

        every { runStatusProvider.updateRunStatus(any(), capture(runStateSlot)) } answers { }

        shutdownService.onDestroy()

        verify {
            runStatusProvider.updateRunStatus(
                eq(assignment.runId.toString()),
                match {
                    it.state == FINISHED &&
                        it.workspaceId == assignment.workspaceId.toString() &&
                        it.connector == GITHUB_ENTERPRISE_CONNECTOR &&
                        it.orgName == assignment.organizationName &&
                        it.message == "Gracefully stopped github enterprise"
                }
            )
        }
    }
}
