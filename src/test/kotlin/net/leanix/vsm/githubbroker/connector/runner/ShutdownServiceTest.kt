package net.leanix.vsm.githubbroker.connector.runner

import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import net.leanix.vsm.githubbroker.connector.application.RunService
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.shared.cache.AssignmentCache
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ShutdownServiceTest {
    private val runService: RunService = mockk(relaxed = true)
    private val shutdownService = ShutdownService(runService)

    @BeforeEach
    fun setup() {
        clearAllMocks()
        AssignmentCache.deleteAll()
    }

    @Test
    fun `test onDestroy should call finish runs`() {
        val assignment = Assignment(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), null, "mock-org")
        AssignmentCache.addAll(listOf(assignment))

        val finishMessageSlot = slot<String>()

        every { runService.finishRuns(capture(finishMessageSlot)) } answers { }

        shutdownService.onDestroy()

        verify {
            runService.finishRuns(
                eq("Gracefully stopped github enterprise"),
            )
        }
    }
}
