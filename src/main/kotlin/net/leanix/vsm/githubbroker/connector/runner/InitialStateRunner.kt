package net.leanix.vsm.githubbroker.connector.runner

import net.leanix.vsm.githubbroker.connector.application.GetInitialState
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner(private val getInitialState: GetInitialState) : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        getInitialState()
    }
}
