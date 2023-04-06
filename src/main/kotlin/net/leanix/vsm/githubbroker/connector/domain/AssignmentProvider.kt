package net.leanix.vsm.githubbroker.connector.domain

interface AssignmentProvider {
    fun getAssignments(integrationName: String, configSetName: String, brokerVersion: String): Result<List<Assignment>>
}
