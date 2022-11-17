package net.leanix.vsm.githubbroker.logs.application

import net.leanix.vsm.githubbroker.logs.adapter.feign.LoggingClient
import net.leanix.vsm.githubbroker.logs.adapter.data.AdminRequest
import net.leanix.vsm.githubbroker.logs.adapter.data.StatusRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LoggingService(
    private val loggingClient: LoggingClient
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoggingService::class.java)
    }

    fun sendStatusLog(statusLog: StatusRequest) {
        logger.info("Sending status log")
        loggingClient.sendStatusLog(statusLog)
    }

    fun sendAdminLog(adminLog: AdminRequest) {
        logger.info("Sending admin log")
        loggingClient.sendAdminLog(adminLog)
    }
}
