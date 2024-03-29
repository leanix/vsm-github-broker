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
    private val doraProvider: DoraProvider,
) : BaseConnectorService() {

    private val logger = LoggerFactory.getLogger(DoraService::class.java)

    @Value("\${leanix.vsm.dora.total-days:30}")
    val periodInDays: Long = 0

    @Async
    fun generateDoraEvents(repository: Repository, assignment: Assignment) {
        val periodInDaysInString = LocalDate.now().minusDays(periodInDays).toString()
        githubRepositoryProvider.getDoraRawData(repository, periodInDaysInString)
            .map {
                if (it.isEmpty()) {
                    logger.info(
                        "Repository does not have any valid pull requests for DORA metrics. " +
                            "Repository: ${repository.name}",
                    )
                } else {
                    logInfoMessages(
                        code = "vsm.dora.success",
                        arguments = arrayOf(repository.name),
                        assignment = assignment,
                    )
                    it.forEach { dora ->
                        doraProvider.saveDora(dora, assignment, repository)
                    }
                }
            }
    }
}
