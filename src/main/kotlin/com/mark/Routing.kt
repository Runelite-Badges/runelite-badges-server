package com.mark

import com.mark.Application.Companion.UNKNOWN
import com.mark.data.BadgeType
import com.mark.data.PluginData
import com.mark.data.PluginManager
import com.mark.data.PluginManager.pluginInfo
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object Routing {

    fun Route.configureRouting() {
        get("/{plugin}/{type}/") {
            val info = pluginInfo.get(call.parameters["plugin"], PluginManager::getAllServers)!!

            val plugin : PluginData = pluginInfo.get("all", PluginManager::getAllServers).get().firstOrNull {
                it.internalName == call.parameters["plugin"].toString()
            } ?: UNKNOWN

            val type = call.parameters["type"].toString().uppercase()
            val badgeType = BadgeType.getType(type)

            if (info.isPresent && badgeType != BadgeType.ERROR) {
                call.respondText { plugin.getBadge(badgeType) }
            } else {
                call.respondText { UNKNOWN.badgeData[badgeType].toString() }
            }
        }
        get("/{plugin}/") {
            val info = pluginInfo.get(call.parameters["plugin"], PluginManager::getAllServers)!!

            val plugin : PluginData = pluginInfo.get("all", PluginManager::getAllServers).get().firstOrNull {
                it.internalName == call.parameters["plugin"].toString()
            } ?: UNKNOWN

            if (info.isPresent) {
                call.respondText { plugin.getBadge(BadgeType.INSTALLS) }
            } else {
                call.respondText { UNKNOWN.badgeData[BadgeType.INSTALLS].toString() }
            }
        }
    }


}
