package net.leanix.vsm.githubbroker.connector.domain

interface TopicProvider {

    fun save(topic: Topic, assignment: Assignment)
}
