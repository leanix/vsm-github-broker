package net.leanix.vsm.githubbroker.connector.adapter.feign

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.LanguageRequest
import net.leanix.vsm.githubbroker.connector.domain.Assignment
import net.leanix.vsm.githubbroker.connector.domain.Language
import net.leanix.vsm.githubbroker.connector.domain.LanguageProvider
import net.leanix.vsm.githubbroker.shared.Constants.GITHUB_ENTERPRISE
import org.springframework.stereotype.Component

@Component
class FeignLanguageProvider(private val languageClient: LanguageClient) : LanguageProvider {

    override fun save(language: Language, assignment: Assignment) {
        val languageToBeSaved = LanguageRequest(
            id = language.id,
            runId = assignment.runId,
            source = GITHUB_ENTERPRISE,
            name = language.name,
            organizationName = assignment.organizationName
        )

        languageClient.saveLanguage(languageToBeSaved)
    }
}
