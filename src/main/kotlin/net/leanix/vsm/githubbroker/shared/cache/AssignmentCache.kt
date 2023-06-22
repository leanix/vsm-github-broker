package net.leanix.vsm.githubbroker.shared.cache

import net.leanix.vsm.githubbroker.connector.domain.Assignment

object AssignmentCache {

    private val assigmentCache: MutableMap<String, Assignment> = mutableMapOf()

    fun addAll(newAssignments: List<Assignment>) {
        newAssignments.forEach { assignment -> assigmentCache[assignment.organizationName] = assignment }
    }

    fun get(key: String): Assignment? {
        return assigmentCache[key]
    }

    fun getAll(): Map<String, Assignment> {
        return assigmentCache
    }

    fun deleteAll() {
        assigmentCache.clear()
    }
}
