package com.mark

import com.mark.Routing.configureRouting
import com.mark.data.BadgeType
import com.mark.data.PluginData
import com.mashape.unirest.http.Unirest
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

class Application  {

    companion object {
        var runeliteVersion : String = ""
        val UNKNOWN = PluginData(
            createdAt = 0,
            lastUpdatedAt = 0,
            installs = 0,
            displayName = "",
            internalName = "",
            version = "",
            description = "",
            support = "",
            tags  = emptyList(),
            hasIcon = false,
        )

    }

    private fun makeDefaultBadges() {
        BadgeType.values().forEach {
            UNKNOWN.getBadge(it,true)
        }
    }

    fun initialize() {
        makeDefaultBadges()
        embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
            getLatestRuneLiteVersion()
            routing {
                configureRouting()
            }
        }.start(wait = true)
    }

    private fun getLatestRuneLiteVersion() {
        val data = Unirest.get("https://api.github.com/repos/runelite/runelite/tags").asJson()
        val array = data.body.array.getJSONObject(0).getString("name")
        runeliteVersion = array.replace("runelite-parent-","")
    }


}

fun main() {
    Application().initialize()
}

