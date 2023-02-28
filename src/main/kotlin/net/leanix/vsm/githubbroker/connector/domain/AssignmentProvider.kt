package net.leanix.vsm.githubbroker.connector.domain

interface AssignmentProvider {
    fun getAssignments(integrationName: String): Result<List<Assignment>>
}
