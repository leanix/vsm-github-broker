package net.leanix.vsm.githubbroker.connector.adapter.json.data

data class PullRequestData(
    val merged: Boolean?
) {
    fun isMerged() = merged != null && merged
}
