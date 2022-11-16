package net.leanix.vsm.githubbroker.adapter.output.service

import net.leanix.vsm.githubbroker.adapter.dto.AdminRequest
import net.leanix.vsm.githubbroker.adapter.dto.StatusRequest
import net.leanix.vsm.githubbroker.adapter.output.client.LoggingClient
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

    fun logStatus(status: StatusRequest) {
        logger.info("Sending status log")
        loggingClient.statusLog(status)
    }

    fun logAdmin(adminLog: AdminRequest) {
        logger.info("Sending admin log")
        loggingClient.adminLog(adminLog)
    }
}
