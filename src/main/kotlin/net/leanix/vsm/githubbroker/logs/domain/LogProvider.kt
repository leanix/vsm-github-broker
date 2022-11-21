package net.leanix.vsm.githubbroker.logs.domain

interface LogProvider {
    fun sendAdminLog(adminLog: AdminLog)
    fun sendStatusLog(statusLog: StatusLog)
}
