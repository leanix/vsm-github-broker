package net.leanix.vsm.githubbroker.connector.adapter.feign.data

data class DoraChangeEventRequest(
    val id: String,
    val sourceType: String,
    val sourceInstance: String,
    val serviceName: String,
    val data: DoraChangeEventData
)

data class DoraChangeEventData(
    val name: String,
    val email: String,
    val username: String?,
    val changeTime: String,
    val repositoryUrl: String
)
