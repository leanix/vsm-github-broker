package net.leanix.vsm.githubbroker.logs.application

import net.leanix.vsm.githubbroker.logs.domain.AdminLog
import net.leanix.vsm.githubbroker.logs.domain.LogProvider
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LoggingService(
    private val logProvider: LogProvider
) {
    private val logger: Logger = LoggerFactory.getLogger(LoggingService::class.java)

    fun sendStatusLog(statusLog: StatusLog) {
        logger.info("Sending status log with runId: ${statusLog.runId}")
        logProvider.sendStatusLog(statusLog)
    }

    fun sendAdminLog(adminLog: AdminLog) {
        logger.info("Sending admin log with runId: ${adminLog.runId}")
        logProvider.sendAdminLog(adminLog)
    }
}
