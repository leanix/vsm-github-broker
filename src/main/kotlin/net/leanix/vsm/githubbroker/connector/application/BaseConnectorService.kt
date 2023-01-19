package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.logs.application.LoggingService
import net.leanix.vsm.githubbroker.logs.domain.LogStatus
import net.leanix.vsm.githubbroker.logs.domain.StatusLog
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

open class BaseConnectorService {

    @Autowired
    private lateinit var loggingService: LoggingService

    private val logger = LoggerFactory.getLogger(BaseConnectorService::class.java)

    fun logFailedStatus(message: String, runId: UUID) {
        logger.error(message)
        loggingService.sendStatusLog(
            StatusLog(runId, LogStatus.FAILED, message)
        )
    }
}
