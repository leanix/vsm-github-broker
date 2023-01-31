package net.leanix.vsm.githubbroker.connector.application

import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.DoraProvider
import net.leanix.vsm.githubbroker.connector.domain.GithubRepositoryProvider
import net.leanix.vsm.githubbroker.connector.domain.Repository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class DoraService(
    private val githubRepositoryProvider: GithubRepositoryProvider,
    private val doraProvider: DoraProvider
) {

    private val logger = LoggerFactory.getLogger(DoraService::class.java)

    @Value("\${leanix.vsm.dora.total-days:30}")
    val totalDoraDays: Long = 0

    @Async
    fun generateDoraEvents(repository: Repository, assignment: Assignment) {
        val last30Days = LocalDate.now().minusDays(totalDoraDays).toString()
        githubRepositoryProvider.getDoraRawData(repository, last30Days)
            .map {
                if (it.isEmpty()) {
                    logger.info(
                        "Repository does not have any valid pull requests for DORA metrics. " +
                            "Repository: ${repository.name}"
                    )
                } else {
                    it.forEach { dora ->
                        doraProvider.saveDora(dora, assignment)
                    }
                }
            }
    }
}
