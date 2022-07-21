package com.mark.data

import com.beust.klaxon.Klaxon
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mark.Application.Companion.runeliteVersion
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import org.apache.commons.lang3.StringUtils
import java.io.InputStream
import java.util.*
import java.util.concurrent.TimeUnit

data class PluginData(
    val createdAt : Int = 0,
    val lastUpdatedAt : Int = 0,
    var installs : Int = 0,
    val displayName : String = "",
    val internalName : String = "",
    val version : String = "",
    val description : String = "",
    val support : String = "",
    val tags : List<String> = emptyList(),
    val hasIcon : Boolean = false,
    val badgeData : MutableMap<BadgeType,String> = emptyMap<BadgeType,String>().toMutableMap()
) {

    fun getBadge(type : BadgeType, error : Boolean = false) : String {
        if(badgeData.containsKey(type)) {
            return badgeData[type].toString()
        }

        println("Make badge")

        val template = BadgeTemplate()

        when(type) {
            BadgeType.INSTALLS -> {
                template.label = "Installs"
                template.message = installs.toString()
            }
            BadgeType.VERSION -> {
                template.label = "Version"
                template.message = version
            }
            BadgeType.ERROR -> {
                template.label = "error"
                template.message = "unknown"
            }
        }
        template.isError = error

        badgeData[type] = Klaxon().toJsonString(template)
        return badgeData[type].toString()
    }
}

object PluginManager {

    val pluginInfo: Cache<String, Optional<List<PluginData>>> = Caffeine.newBuilder()
        .maximumSize(3000)
        .expireAfterWrite(1, TimeUnit.HOURS)
    .build(PluginManager::getAllServers)

    fun getAllServers(name: String = "all") : Optional<List<PluginData>> {
        val latestManifest = getLatestManifest()
        val installs = getInstalls()
        latestManifest.forEach {
            it.installs = installs[it.internalName.ifEmpty { it.displayName }] ?: 0
        }
        return Optional.of(latestManifest)
    }

    private fun getLatestManifest(): List<PluginData> {
        val response: HttpResponse<InputStream> = Unirest.get(
            "https://repo.runelite.net/plugins/$runeliteVersion/manifest.js"
        ).asBinary()
        val content = response.rawBody.reader().readText()
        val json = "[{\"${StringUtils.substringAfter(content,"[{\"")}"
        return Klaxon().parseArray(json)!!
    }

    private fun getInstalls(): Map<String,Int> {
        val response: HttpResponse<JsonNode> = Unirest.get(
            "https://api.runelite.net/runelite-${runeliteVersion}/pluginhub"
        ).asJson()
        return Klaxon().parse<Map<String,Int>>(response.rawBody.reader())!!
    }


}