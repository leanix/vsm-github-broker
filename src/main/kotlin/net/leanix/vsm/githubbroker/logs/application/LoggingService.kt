package net.leanix.vsm.githubbroker.logs.application


import net.leanix.vsm.githubbroker.logs.adapter.feign.data.StatusRequest
import net.leanix.vsm.githubbroker.logs.domain.LogAdmin
import net.leanix.vsm.githubbroker.logs.domain.LogProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class LoggingService(
    private val logProvider: LogProvider
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(LoggingService::class.java)
    }

    fun sendStatusLog(statusLog: StatusRequest) {
        logger.info("Sending status log")
        loggingClient.sendStatusLog(statusLog)
    }

    fun sendAdminLog(adminLog: LogAdmin) {
        logger.info("Sending admin log")
        logProvider.saveAdminLog(adminLog)
    }
}
