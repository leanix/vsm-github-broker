package net.leanix.vsm.githubbroker.connector.domain

interface CommandProvider {

    fun sendCommand(assignment: Assignment, action: CommandEventAction)
}
