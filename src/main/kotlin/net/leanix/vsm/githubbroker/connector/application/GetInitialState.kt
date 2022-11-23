package net.leanix.vsm.githubbroker.connector.application

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class GetInitialState(private val getAssignment: GetAssignment) {

    private val logger: Logger = LoggerFactory.getLogger(GetInitialState::class.java)

    operator fun invoke() {
        kotlin.runCatching {
            getAssignment()
        }.onFailure {
            logger.error("Failed to get initial state")
        }
    }
}
