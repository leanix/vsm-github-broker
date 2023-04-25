package net.leanix.vsm.githubbroker.connector.domain

interface DoraProvider {

    fun saveDora(dora: Dora, assignment: Assignment, repository: Repository)
}
