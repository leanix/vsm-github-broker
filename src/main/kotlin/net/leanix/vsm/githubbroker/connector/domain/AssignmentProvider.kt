package net.leanix.vsm.githubbroker.connector.domain

interface AssignmentProvider {

    fun getAssignment(integrationName: String, configurationName: String): Result<Assignment>
}
