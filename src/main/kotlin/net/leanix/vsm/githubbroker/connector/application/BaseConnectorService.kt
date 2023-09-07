package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.AdminLog
import net.leanix.vsm.githubbroker.logs.domain.LogLevel
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import java.util.Locale
import java.util.UUID

open class BaseConnectorService {

    @Autowired
    private lateinit var loggingService: LoggingService

    @Autowired
    private lateinit var messageSource: MessageSource

    private val logger = LoggerFactory.getLogger(BaseConnectorService::class.java)

    fun logFailedStatus(message: String? = "empty message", runId: UUID) {
        logger.error(message)
        loggingService.sendStatusLog(
            StatusLog(runId, LogStatus.FAILED, message)
        )
    }

    fun logInfoStatus(message: String? = "", runId: UUID, status: LogStatus) {
        logger.info(message)
        loggingService.sendStatusLog(
            StatusLog(runId, status, message)
        )
    }

    fun logInfoMessages(code: String, arguments: Array<Any>, assignment: Assignment) {
        val message = messageSource.getMessage(
            code,
            arguments,
            Locale.ENGLISH
        )
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.INFO.toString(),
                level = LogLevel.INFO,
                message = message
            )
        )
    }

    fun logInfoMessages(message: String, assignment: Assignment) {
        logger.info(message)
        loggingService.sendAdminLog(
                AdminLog(
                        runId = assignment.runId,
                        configurationId = assignment.configurationId,
                        subject = LogLevel.INFO.toString(),
                        level = LogLevel.INFO,
                        message = message
                )
        )
    }

    fun logFailedMessages(code: String, arguments: Array<Any>, assignment: Assignment) {
        val message = messageSource.getMessage(
            code,
            arguments,
            Locale.ENGLISH
        )
        loggingService.sendAdminLog(
            AdminLog(
                runId = assignment.runId,
                configurationId = assignment.configurationId,
                subject = LogLevel.ERROR.toString(),
                level = LogLevel.ERROR,
                message = message
            )
        )
    }
}
