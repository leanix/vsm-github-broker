package net.leanix.vsm.githubbroker.connector.domain

interface LanguageProvider {

    fun save(language: Language, assignment: Assignment)
}
