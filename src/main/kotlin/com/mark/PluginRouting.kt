package com.mark

import com.beust.klaxon.Klaxon
import com.mark.Application.Companion.UNKNOWN
import com.mark.data.BadgeTypePlugin
import com.mark.data.PluginData
import com.mark.data.PluginManager
import com.mark.data.PluginManager.getAllServers
import com.mark.data.PluginManager.pluginInfo
import com.mark.data.PluginManager.serverRanking
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object PluginRouting {

    fun Route.configureRouting() {
        get("/shields/{type}/plugin/{name}/") {
            val info = pluginInfo.get(call.parameters["name"], PluginManager::getAllServers)!!

            val plugin : PluginData = pluginInfo.get("all", PluginManager::getAllServers).get().firstOrNull {
                it.internalName == call.parameters["name"].toString()
            } ?: UNKNOWN

            val type = call.parameters["type"].toString().uppercase()
            val badgeType = BadgeTypePlugin.getType(type)

            if (info.isPresent && badgeType != BadgeTypePlugin.ERROR) {
                call.respondText { plugin.getBadgePlugin(badgeType) }
            } else {
                call.respondText { UNKNOWN.badgeData[badgeType].toString() }
            }
        }
        get("/api/ranking/plugin/") {
            if(serverRanking.isEmpty()) {
                getAllServers()
            }
            call.respondText { Klaxon().toJsonString(serverRanking) }
        }
        get("/shields/plugin/{name}/") {
            val info = pluginInfo.get(call.parameters["name"], PluginManager::getAllServers)!!

            val plugin : PluginData = pluginInfo.get("all", PluginManager::getAllServers).get().firstOrNull {
                it.internalName == call.parameters["name"].toString()
            } ?: UNKNOWN

            if (info.isPresent) {
                call.respondText { plugin.getBadgePlugin(BadgeTypePlugin.INSTALLS) }
            } else {
                call.respondText { UNKNOWN.badgeData[BadgeTypePlugin.INSTALLS].toString() }
            }
        }
    }


}
