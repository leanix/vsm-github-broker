package net.leanix.vsm.githubbroker.logs.application

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.leanix.vsm.githubbroker.logs.domain.AdminLog
import net.leanix.vsm.githubbroker.logs.domain.LogLevel
import net.leanix.vsm.githubbroker.logs.domain.LogProvider
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
import org.junit.jupiter.api.Test
import java.util.UUID

class LoggingServiceTest {

    private val logProvider = mockk<LogProvider>()
    private val loggingService = LoggingService(logProvider)
    @Test
    fun `sending status log should call correct client`() {
        val statusLog = StatusLog(
            runId = UUID.randomUUID(),
            status = LogStatus.SUCCESSFUL,
            message = "Success"
        )
        every { logProvider.sendStatusLog(any()) } returns Unit
        loggingService.sendStatusLog(statusLog)
        verify(exactly = 1) { logProvider.sendStatusLog(statusLog) }
    }
    @Test
    fun `sending admin log should call correct client`() {
        val adminLog = AdminLog(
            runId = UUID.randomUUID(),
            configurationId = UUID.randomUUID(),
            subject = "dummy",
            level = LogLevel.INFO,
            message = "dummy"
        )
        every { logProvider.sendAdminLog(any()) } returns Unit
        loggingService.sendAdminLog(adminLog)
        verify(exactly = 1) { logProvider.sendAdminLog(adminLog) }
    }
}
