package net.leanix.vsm.githubbroker.logs.domain

interface LogProvider {

    fun saveAdminLog(logAdmin: LogAdmin)
}
