package com.mark

import com.mark.PluginRouting.configureRouting
import com.mark.data.BadgeTypePlugin
import com.mark.data.PluginData
import com.mashape.unirest.http.Unirest
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

class Application  {

    companion object {
        var runeliteVersion : String = ""
        val UNKNOWN = PluginData()

    }

    private fun makeDefaultBadges() {
        BadgeTypePlugin.values().forEach {
            UNKNOWN.getBadgePlugin(it,true)
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

