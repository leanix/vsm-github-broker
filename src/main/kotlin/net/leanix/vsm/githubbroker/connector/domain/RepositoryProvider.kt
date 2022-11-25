package net.leanix.vsm.githubbroker.connector.domain

interface RepositoryProvider {

    fun save(repository: Repository, assignment: Assignment)
}
