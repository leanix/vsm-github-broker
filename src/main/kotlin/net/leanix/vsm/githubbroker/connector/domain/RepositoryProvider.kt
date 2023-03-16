package net.leanix.vsm.githubbroker.connector.domain

import net.leanix.vsm.githubbroker.connector.adapter.feign.data.EventType

interface RepositoryProvider {

    fun save(repository: Repository, assignment: Assignment, eventType: EventType)
}
