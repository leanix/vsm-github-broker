package net.leanix.vsm.githubbroker.connector.domain

data class Repository(
    val id: String,
    val name: String,
    val description: String?,
    val url: String,
    val archived: Boolean?,
    val languages: List<Language>?,
    val topics: List<Topic>?
)
