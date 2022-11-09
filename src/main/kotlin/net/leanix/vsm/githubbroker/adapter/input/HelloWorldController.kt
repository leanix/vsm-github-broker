package net.leanix.vsm.githubbroker.adapter.input

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloWorldController {
    companion object {
        const val HELLO_WORLD = "Hello World"
    }

    @GetMapping
    fun getHelloWorld(): String {
        return HELLO_WORLD
    }
}
