package net.leanix.vsm.githubbroker.connector.adapter.feign.data

import com.fasterxml.jackson.annotation.JsonValue

enum class EventType(@JsonValue val type: String) {
    STATE("state"),
    CHANGE("change"),
    COMMAND("command");

    companion object {
        fun from(type: String?): EventType? = EventType.values().firstOrNull { it.type == type }
    }
}
