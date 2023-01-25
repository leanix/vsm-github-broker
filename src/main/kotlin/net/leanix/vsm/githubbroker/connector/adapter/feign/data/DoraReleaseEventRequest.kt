package net.leanix.vsm.githubbroker.connector.adapter.feign.data

data class DoraReleaseEventRequest(
    val id: String,
    val sourceType: String,
    val sourceInstance: String,
    val serviceName: String,
    val data: DoraReleaseEventData
)

data class DoraReleaseEventData(
    val changeIds: List<String>,
    val releaseTime: String
)
