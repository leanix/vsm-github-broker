package net.leanix.vsm.githubbroker.connector.runner

import jakarta.annotation.PreDestroy
import net.leanix.vsm.githubbroker.connector.application.RunService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShutdownService(private val runService: RunService) {

    private val logger = LoggerFactory.getLogger(ShutdownService::class.java)

    @PreDestroy
    fun onDestroy() {
        logger.info("Shutting down github broker")
        runService.finishRuns("Gracefully stopped github enterprise")
    }
}
