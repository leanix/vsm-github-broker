package net.leanix.vsm.githubbroker.logs.adapter.feign

import net.leanix.vsm.githubbroker.logs.adapter.feign.data.AdminRequest
import net.leanix.vsm.githubbroker.logs.domain.LogAdmin
import net.leanix.vsm.githubbroker.logs.domain.LogProvider

class FeignLogProvider(private val loggingClient: LoggingClient) : LogProvider {

    override fun saveAdminLog(logAdmin: LogAdmin) {
        try {

            loggingClient.sendAdminLog(AdminRequest.fromDomain(logAdmin))
        } catch () {
            log.fsdfsd
        }
    }
}